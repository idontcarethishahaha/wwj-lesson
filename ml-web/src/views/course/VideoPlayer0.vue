<template>
  <div class="video-page">
    <div class="video-layout">
      <div class="video-main">
        <div class="video-wrapper">
          <div class="video-container">
            <video
                ref="videoRef"
                :src="episode?.video ? MINIO_EPISODE_VIDEO(episode.video) : ''"
                controls
                class="video-player"
                @timeupdate="handleTimeUpdate"
                @play="handlePlay"
                @pause="handlePause"
                @loadedmetadata="handleLoadedMetadata"
            >
              您的浏览器不支持视频播放
            </video>
            <div class="danmaku-overlay" ref="danmakuOverlayRef">
              <div
                  v-for="dm in activeDanmakus"
                  :key="dm.id"
                  class="danmaku-item"
                  :style="{
                  top: dm.top + '%',
                  animationDuration: dm.duration + 's',
                  animationDelay: '0s',
                  color: dm.color || '#fff',
                }"
              >
                <span v-if="dm.username" class="danmaku-user">{{ dm.username }}：</span>
                {{ dm.content }}
              </div>
            </div>
          </div>
          <div class="video-controls-bar">
            <div class="speed-control">
              <span class="control-label">播放速度：</span>
              <el-radio-group v-model="playbackRate" size="small" @change="handleSpeedChange">
                <el-radio-button value="0.5">0.5x</el-radio-button>
                <el-radio-button value="1">1x</el-radio-button>
                <el-radio-button value="1.5">1.5x</el-radio-button>
                <el-radio-button value="2">2x</el-radio-button>
              </el-radio-group>
            </div>
            <div class="danmaku-input-wrapper">
              <el-input
                  v-model="danmakuContent"
                  placeholder="发送弹幕..."
                  size="small"
                  class="danmaku-input"
                  maxlength="100"
                  @keyup.enter="handleSendDanmaku"
              />
              <el-button type="primary" size="small" @click="handleSendDanmaku">发送</el-button>
            </div>
          </div>
        </div>

        <div class="episode-info-section">
          <h3 class="episode-title">{{ episode?.title }}</h3>
          <p class="episode-meta">
            <span>时长：{{ episode ? formatDuration(episode.duration) : '--:--' }}</span>
            <span>总弹幕：{{ allDanmakus.length }}</span>
          </p>
        </div>

        <div class="comment-section">
          <h3 class="section-title">课程评论</h3>
          <div class="comment-form">
            <el-input
                v-model="commentContent"
                type="textarea"
                :rows="3"
                placeholder="写下你的评论..."
                maxlength="500"
                show-word-limit
            />
            <div class="comment-form-actions">
              <el-button type="primary" :loading="commentSubmitting" @click="handlePostComment">
                发布评论
              </el-button>
            </div>
          </div>

          <div v-if="comments.length === 0" class="empty-section">
            <el-empty description="暂无评论" :image-size="80" />
          </div>

          <div v-else class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <el-avatar :size="40" :src="comment.avatar">
                {{ comment.nickname?.charAt(0) || '?' }}
              </el-avatar>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-username">{{ comment.nickname }}</span>
                  <span class="comment-time">{{ comment.createTime }}</span>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
                <div class="comment-actions">
                  <el-button
                      text
                      size="small"
                      :type="comment.liked ? 'danger' : 'default'"
                      :icon="comment.liked ? 'HeartFilled' : 'Heart'"
                      @click="handleLikeComment(comment)"
                  >
                    {{ comment.likeCount }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="commentTotal > comments.length" class="load-more">
            <el-button text type="primary" :loading="commentLoading" @click="loadMoreComments">
              加载更多评论
            </el-button>
          </div>
        </div>
      </div>

      <div class="video-sidebar">
        <div class="sidebar-header">
          <h3>课程目录</h3>
        </div>
        <div class="sidebar-content">
          <el-collapse v-model="activeSeason" accordion>
            <el-collapse-item
                v-for="season in seasons"
                :key="season.id"
                :title="season.title"
                :name="season.id"
            >
              <div
                  v-for="ep in season.episodes"
                  :key="ep.id"
                  class="sidebar-episode"
                  :class="{ active: ep.id === episodeId }"
                  @click="switchEpisode(ep.id)"
              >
                <div class="sidebar-episode-info">
                  <el-icon v-if="ep.free"><VideoCamera /></el-icon>
                  <el-icon v-else><Lock /></el-icon>
                  <span class="sidebar-episode-title">{{ ep.title }}</span>
                </div>
                <span class="sidebar-episode-duration">{{ formatDuration(ep.duration) }}</span>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoCamera, Lock, HeartFilled } from '@element-plus/icons-vue'
import { cmsApi } from '@/api'
import { barrageService } from '@/api/barrage'
import { MINIO_EPISODE_VIDEO } from '@/const'
import { useUserStore } from '@/stores/user'
import type { Episode, Season, Danmaku, Comment } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const courseId = Number(route.params.courseId)
const episodeId = ref(Number(route.params.episodeId))

const videoRef = ref<HTMLVideoElement | null>(null)
const danmakuOverlayRef = ref<HTMLDivElement | null>(null)

const episode = ref<Episode | null>(null)
const seasons = ref<Season[]>([])
const activeSeason = ref<number | undefined>(undefined)

const allDanmakus = ref<Danmaku[]>([])
const activeDanmakus = ref<(Danmaku & { top: number; duration: number; color?: string })[]>([])
const danmakuContent = ref('')
const playbackRate = ref('1')
const currentTime = ref(0)
const isPlaying = ref(false)
const isConnected = ref(false)

const comments = ref<Comment[]>([])
const commentPage = ref(1)
const commentTotal = ref(0)
const commentLoading = ref(false)
const commentContent = ref('')
const commentSubmitting = ref(false)

let danmakuTimer: ReturnType<typeof setInterval> | null = null
let processedDanmakuIds = new Set<number>()

function formatDuration(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) {
    return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
  }
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

async function fetchEpisode() {
  try {
    const res = await cmsApi.getEpisode(episodeId.value)
    episode.value = res.data
  } catch {
    episode.value = null
    ElMessage.error('获取视频信息失败')
  }
}

async function fetchSeasons() {
  try {
    const res = await cmsApi.getSeasons(courseId)
    seasons.value = res.data
    if (res.data.length > 0) {
      activeSeason.value = res.data[0].id
    }
  } catch {
    seasons.value = []
  }
}

async function fetchDanmakus() {
  allDanmakus.value = []
  processedDanmakuIds.clear()
}

function handleSpeedChange(val: string) {
  if (videoRef.value) {
    videoRef.value.playbackRate = Number(val)
  }
}

function handleTimeUpdate() {
  if (videoRef.value) {
    currentTime.value = videoRef.value.currentTime
  }
}

function handlePlay() {
  isPlaying.value = true
  startDanmakuCheck()
}

function handlePause() {
  isPlaying.value = false
  stopDanmakuCheck()
}

function handleLoadedMetadata() {
  if (videoRef.value) {
    videoRef.value.playbackRate = Number(playbackRate.value)
  }
}

function startDanmakuCheck() {
  if (danmakuTimer) return
  danmakuTimer = setInterval(() => {
    if (!isPlaying.value || !videoRef.value) return
    const ct = videoRef.value.currentTime
    const overlay = danmakuOverlayRef.value
    if (!overlay) return

    const candidates = allDanmakus.value.filter(
        dm => !processedDanmakuIds.has(dm.id) && Math.abs(dm.time - ct) <= 0.5
    )

    for (const dm of candidates) {
      processedDanmakuIds.add(dm.id)
      const top = 10 + Math.random() * 60
      const colors = ['#fff', '#ff0', '#0ff', '#f0f', '#0f0', '#f80']
      const color = colors[Math.floor(Math.random() * colors.length)]
      const duration = 5 + Math.random() * 3
      activeDanmakus.value.push({ ...dm, top, duration, color })
    }

    if (activeDanmakus.value.length > 100) {
      activeDanmakus.value = activeDanmakus.value.slice(-100)
    }
  }, 200)
}

function stopDanmakuCheck() {
  if (danmakuTimer) {
    clearInterval(danmakuTimer)
    danmakuTimer = null
  }
}

function getCurrentVideoTime(): number {
  return videoRef.value ? videoRef.value.currentTime : 0
}

async function handleSendDanmaku() {
  if (!danmakuContent.value.trim()) {
    ElMessage.warning('请输入弹幕内容')
    return
  }
  const time = getCurrentVideoTime()
  try {
    barrageService.send({
      episodeId: episodeId.value,
      content: danmakuContent.value.trim(),
      time,
    })
    ElMessage.success('弹幕已发送')
    const newDm: Danmaku = {
      id: Date.now(),
      episodeId: episodeId.value,
      userId: userStore.userInfo?.id || 0,
      username: userStore.userInfo?.nickname || '我',
      content: danmakuContent.value.trim(),
      time,
      createTime: new Date().toISOString(),
    }
    allDanmakus.value.push(newDm)
    const overlay = danmakuOverlayRef.value
    if (overlay && isPlaying.value) {
      const top = 10 + Math.random() * 60
      const duration = 5 + Math.random() * 3
      const colors = ['#fff', '#ff0', '#0ff', '#f0f', '#0f0', '#f80']
      const color = colors[Math.floor(Math.random() * colors.length)]
      activeDanmakus.value.push({ ...newDm, top, duration, color })
    }
    danmakuContent.value = ''
  } catch (error) {
    ElMessage.error('弹幕发送失败：' + (error as Error).message)
  }
}

function switchEpisode(id: number) {
  router.push(`/courses/${courseId}/video/${id}`)
}

async function fetchComments() {
  commentLoading.value = true
  try {
    const res = await cmsApi.getComments({
      pageNum: commentPage.value,
      pageSize: 10,
      fkEpisodeId: episodeId.value,
    })
    if (commentPage.value === 1) {
      comments.value = res.data.records
    } else {
      comments.value = [...comments.value, ...res.data.records]
    }
    commentTotal.value = res.data.total
  } catch {
    if (commentPage.value === 1) {
      comments.value = []
    }
    commentTotal.value = 0
  } finally {
    commentLoading.value = false
  }
}

function loadMoreComments() {
  commentPage.value++
  fetchComments()
}

async function handlePostComment() {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  commentSubmitting.value = true
  try {
    await cmsApi.postComment({
      fkEpisodeId: episodeId.value,
      fkUserId: userStore.userInfo?.id || 0,
      pid: 0,
      content: commentContent.value.trim(),
    })
    ElMessage.success('评论发布成功')
    commentContent.value = ''
    commentPage.value = 1
    await fetchComments()
  } catch {
    ElMessage.error('评论发布失败')
  } finally {
    commentSubmitting.value = false
  }
}

async function handleLikeComment(comment: Comment) {
  try {
    await cmsApi.likeComment(comment.id)
    comment.liked = !comment.liked
    comment.likeCount += comment.liked ? 1 : -1
  } catch {
    ElMessage.error('操作失败')
  }
}

watch(
    () => route.params.episodeId,
    async (newEpisodeId) => {
      if (newEpisodeId) {
        const newId = Number(newEpisodeId)
        if (newId !== episodeId.value) {
          episodeId.value = newId
          stopDanmakuCheck()
          processedDanmakuIds.clear()
          activeDanmakus.value = []
          danmakuContent.value = ''
          commentPage.value = 1
          await fetchEpisode()
          await fetchDanmakus()
          await fetchComments()
        }
      }
    }
)

async function initBarrage() {
  const userId = userStore.userInfo?.id
  if (!userId) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    await barrageService.connect(userId)
    isConnected.value = true
    
    barrageService.onMessage((data) => {
      const dm: Danmaku = {
        id: Date.now(),
        episodeId: data.episodeId,
        userId: data.userId,
        username: data.username || '匿名用户',
        content: data.content,
        time: data.time,
        createTime: new Date().toISOString(),
      }
      
      allDanmakus.value.push(dm)
      
      const overlay = danmakuOverlayRef.value
      if (overlay) {
        const top = 10 + Math.random() * 60
        const duration = 5 + Math.random() * 3
        const colors = ['#fff', '#ff0', '#0ff', '#f0f', '#0f0', '#f80']
        const color = colors[Math.floor(Math.random() * colors.length)]
        activeDanmakus.value.push({ ...dm, top, duration, color })
      }
    })
    
    ElMessage.success('弹幕服务已连接')
  } catch (error) {
    ElMessage.error('弹幕服务连接失败')
    console.error('弹幕服务连接失败', error)
  }
}

onMounted(async () => {
  await fetchEpisode()
  await fetchSeasons()
  await fetchDanmakus()
  await fetchComments()
  await initBarrage()
})

onBeforeUnmount(() => {
  stopDanmakuCheck()
  barrageService.disconnect()
})
</script>

<style scoped lang="scss">
.video-page {
  background: #1a1a1a;
  min-height: calc(100vh - 120px);
}

.video-layout {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 0;
  padding: 20px;
}

.video-main {
  flex: 1;
  min-width: 0;
}

.video-wrapper {
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  background: #000;

  .video-player {
    width: 100%;
    height: 100%;
    display: block;
    object-fit: contain;
  }
}

.danmaku-overlay {
  position: absolute;
  top: 60px;
  left: 0;
  right: 0;
  bottom: 80px;
  pointer-events: none;
  overflow: hidden;
}

.danmaku-item {
  position: absolute;
  white-space: nowrap;
  font-size: 16px;
  font-weight: 500;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
  animation: danmakuMove linear forwards;
  left: 100%;
  pointer-events: none;
  line-height: 1.4;

  .danmaku-user {
    opacity: 0.8;
  }
}

@keyframes danmakuMove {
  0% {
    left: 100%;
    transform: translateX(0);
  }
  100% {
    left: 0;
    transform: translateX(-100%);
  }
}

.video-controls-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #2a2a2a;
  flex-wrap: wrap;
  gap: 12px;

  .speed-control {
    display: flex;
    align-items: center;
    gap: 8px;

    .control-label {
      font-size: 13px;
      color: #ccc;
      white-space: nowrap;
    }

    :deep(.el-radio-button__inner) {
      background: transparent;
      border-color: #444;
      color: #ccc;
      font-size: 12px;
      padding: 4px 10px;
    }

    :deep(.el-radio-button.is-active .el-radio-button__inner) {
      background: #409EFF;
      border-color: #409EFF;
      color: #fff;
    }
  }

  .danmaku-input-wrapper {
    display: flex;
    gap: 8px;
    flex: 1;
    max-width: 400px;

    .danmaku-input {
      flex: 1;
    }

    :deep(.el-input__wrapper) {
      background: #333;
      box-shadow: 0 0 0 1px #444 inset;
    }

    :deep(.el-input__inner) {
      color: #fff;
      &::placeholder {
        color: #888;
      }
    }
  }
}

.episode-info-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  margin-top: 12px;

  .episode-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
  }

  .episode-meta {
    font-size: 13px;
    color: #909399;
    display: flex;
    gap: 20px;
  }
}

.video-sidebar {
  width: 340px;
  flex-shrink: 0;
  margin-left: 20px;
  background: #2a2a2a;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 160px);

  .sidebar-header {
    padding: 16px 20px;
    border-bottom: 1px solid #3a3a3a;
    flex-shrink: 0;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #fff;
      margin: 0;
    }
  }

  .sidebar-content {
    flex: 1;
    overflow-y: auto;

    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background: #555;
      border-radius: 2px;
    }
  }
}

.sidebar-episode {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  margin: 2px 0;

  &:hover {
    background: #3a3a3a;
  }

  &.active {
    background: #409EFF;
    color: #fff;
  }

  .sidebar-episode-info {
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 0;
    flex: 1;

    .el-icon {
      font-size: 14px;
      flex-shrink: 0;
    }

    .sidebar-episode-title {
      font-size: 13px;
      color: #ddd;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  &.active .sidebar-episode-title {
    color: #fff;
  }

  .sidebar-episode-duration {
    font-size: 12px;
    color: #888;
    flex-shrink: 0;
    margin-left: 8px;
  }

  &.active .sidebar-episode-duration {
    color: rgba(255, 255, 255, 0.8);
  }
}

:deep(.el-collapse) {
  border-top: none;
  background: transparent;
}

:deep(.el-collapse-item__header) {
  background: transparent;
  color: #ddd;
  font-size: 14px;
  font-weight: 500;
  padding: 0 12px;
  border-bottom: 1px solid #3a3a3a;
}

:deep(.el-collapse-item__wrap) {
  background: transparent;
  border-bottom: none;
}

:deep(.el-collapse-item__content) {
  padding: 4px 12px 8px;
  color: #ddd;
}

.comment-section {
  padding: 12px;

  .section-title {
    color: #fff;
  }
}

.comment-form-actions {
  margin-top: 16px;
  margin-bottom: 16px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #3a3a3a;
  border-radius: 8px;
}

.comment-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-username {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}

.comment-time {
  font-size: 12px;
  color: #888;
}

.comment-content {
  font-size: 14px;
  color: #ddd;
  line-height: 1.6;
}

.comment-actions {
  display: flex;
  gap: 16px;
}
</style>