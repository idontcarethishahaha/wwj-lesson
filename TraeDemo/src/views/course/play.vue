<template>
  <div class="play-page">
    <div class="play-header">
      <div class="container">
        <div class="header-left">
          <el-button text @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回课程
          </el-button>
          <h2>{{ videoInfo.title }}</h2>
        </div>
        <div class="header-right">
          <el-button-group>
            <el-button :type="playbackRate === 1 ? 'primary' : ''" @click="setPlaybackRate(1)">1x</el-button>
            <el-button :type="playbackRate === 1.5 ? 'primary' : ''" @click="setPlaybackRate(1.5)">1.5x</el-button>
            <el-button :type="playbackRate === 2 ? 'primary' : ''" @click="setPlaybackRate(2)">2x</el-button>
          </el-button-group>
          <el-button :icon="FullScreen" circle @click="toggleFullScreen" />
        </div>
      </div>
    </div>

    <div class="play-container">
      <div class="video-wrapper" ref="videoWrapper">
        <video
          ref="videoPlayer"
          class="video-player"
          :src="videoInfo.videoUrl"
          @timeupdate="handleTimeUpdate"
          @loadedmetadata="handleLoadedMetadata"
          @ended="handleEnded"
          @click="togglePlay"
        ></video>
        <div class="video-controls">
          <el-slider
            v-model="currentTime"
            :max="duration"
            @change="handleSeek"
            :show-tooltip="false"
          />
          <div class="controls-bottom">
            <div class="controls-left">
              <el-button text @click="togglePlay">
                <el-icon v-if="isPlaying"><VideoPause /></el-icon>
                <el-icon v-else><VideoPlay /></el-icon>
              </el-button>
              <span class="time-display">
                {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
              </span>
            </div>
            <div class="controls-right">
              <el-button text @click="toggleDanmaku">
                <el-icon><ChatLineSquare /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
        <div class="danmaku-layer" v-if="showDanmaku">
          <div
            v-for="dm in danmakuList"
            :key="dm.id"
            class="danmaku-item"
            :style="{ top: dm.top + 'px', right: dm.visible ? '0' : '-200px' }"
          >
            {{ dm.text }}
          </div>
        </div>
      </div>

      <div class="sidebar">
        <el-tabs v-model="sidebarTab">
          <el-tab-pane label="目录" name="catalog">
            <div class="chapter-list">
              <div
                v-for="chapter in chapters"
                :key="chapter.id"
                class="chapter-item"
              >
                <div
                  class="chapter-header"
                  :class="{ active: currentChapterId === chapter.id }"
                  @click="toggleChapter(chapter.id)"
                >
                  <span>{{ chapter.title }}</span>
                  <el-icon>
                    <ArrowRight v-if="currentChapterId !== chapter.id" />
                    <ArrowDown v-else />
                  </el-icon>
                </div>
                <div class="chapter-items" v-show="currentChapterId === chapter.id">
                  <div
                    v-for="item in chapter.items"
                    :key="item.id"
                    class="item-row"
                    :class="{ active: itemId === item.id, playing: playingItemId === item.id }"
                    @click="switchItem(item.id)"
                  >
                    <el-icon v-if="playingItemId === item.id" class="playing-icon"><VideoPlay /></el-icon>
                    <span class="item-title">{{ item.title }}</span>
                    <span class="item-duration">{{ item.duration }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="评论" name="comment">
            <div class="comment-section">
              <div class="comment-input">
                <el-input
                  v-model="commentContent"
                  type="textarea"
                  placeholder="发表你的看法..."
                  :rows="3"
                />
                <el-button type="primary" size="small" @click="submitComment" :loading="submitting">
                  发表评论
                </el-button>
              </div>
              <div class="comment-list">
                <div v-for="comment in comments" :key="comment.id" class="comment-item">
                  <el-avatar :size="36" :src="comment.userAvatar">
                    {{ comment.userName?.charAt(0) }}
                  </el-avatar>
                  <div class="comment-body">
                    <div class="comment-header">
                      <span class="user-name">{{ comment.userName }}</span>
                      <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                    </div>
                    <p class="comment-content">{{ comment.content }}</p>
                    <el-button text size="small" type="danger" @click="reportComment(comment.id)">
                      举报
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { courseApi } from '@/api/course'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, ArrowRight, ArrowDown, VideoPlay, VideoPause,
  FullScreen, ChatLineSquare
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const videoPlayer = ref(null)
const videoWrapper = ref(null)

const itemId = computed(() => route.params.id)
const videoInfo = ref({})
const chapters = ref([])
const currentChapterId = ref(null)
const playingItemId = ref(null)
const sidebarTab = ref('catalog')

const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const playbackRate = ref(1)
const showDanmaku = ref(true)
const danmakuList = ref([])

const commentContent = ref('')
const submitting = ref(false)
const comments = ref([])

let danmakuTimer = null

const formatTime = (seconds) => {
  const min = Math.floor(seconds / 60)
  const sec = Math.floor(seconds % 60)
  return `${String(min).padStart(2, '0')}:${String(sec).padStart(2, '0')}`
}

const goBack = () => {
  if (videoInfo.value.courseId) {
    router.push(`/course/detail/${videoInfo.value.courseId}`)
  } else {
    router.push('/course/list')
  }
}

const togglePlay = () => {
  if (!videoPlayer.value) return
  if (isPlaying.value) {
    videoPlayer.value.pause()
  } else {
    videoPlayer.value.play()
  }
  isPlaying.value = !isPlaying.value
}

const handleTimeUpdate = () => {
  if (videoPlayer.value) {
    currentTime.value = videoPlayer.value.currentTime
  }
}

const handleLoadedMetadata = () => {
  if (videoPlayer.value) {
    duration.value = videoPlayer.value.duration
  }
}

const handleEnded = () => {
  isPlaying.value = false
}

const handleSeek = (val) => {
  if (videoPlayer.value) {
    videoPlayer.value.currentTime = val
  }
}

const setPlaybackRate = (rate) => {
  playbackRate.value = rate
  if (videoPlayer.value) {
    videoPlayer.value.playbackRate = rate
  }
}

const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    videoWrapper.value?.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const toggleDanmaku = () => {
  showDanmaku.value = !showDanmaku.value
}

const toggleChapter = (id) => {
  currentChapterId.value = currentChapterId.value === id ? null : id
}

const switchItem = async (id) => {
  if (id === itemId.value) return
  router.push(`/course/play/${id}`)
}

const loadVideoInfo = async () => {
  try {
    const res = await courseApi.getVideoPlayInfo(itemId.value)
    videoInfo.value = res.data || {}
    playingItemId.value = itemId.value

    const chapterRes = await courseApi.getCourseChapters(videoInfo.value.courseId)
    chapters.value = chapterRes.data || []

    const firstChapterWithItems = chapters.value.find(ch => ch.items?.length > 0)
    if (firstChapterWithItems) {
      currentChapterId.value = firstChapterWithItems.id
    }

    await loadComments()
  } catch (error) {
    ElMessage.error('加载视频信息失败')
  }
}

const loadComments = async () => {
  try {
    const res = await courseApi.getComments(itemId.value, { page: 1, limit: 50 })
    comments.value = res.data?.records || []
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  submitting.value = true
  try {
    await courseApi.addComment({
      itemId: itemId.value,
      content: commentContent.value
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    await loadComments()
  } catch (error) {
    ElMessage.error('评论失败')
  } finally {
    submitting.value = false
  }
}

const reportComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要举报这条评论吗？', '举报', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await courseApi.reportComment({ commentId })
    ElMessage.success('举报成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('举报失败')
    }
  }
}

const startDanmaku = () => {
  danmakuTimer = setInterval(() => {
    if (Math.random() > 0.7) {
      const dm = {
        id: Date.now(),
        text: '这是一条弹幕内容',
        top: Math.random() * 300,
        visible: true
      }
      danmakuList.value.push(dm)
      setTimeout(() => {
        dm.visible = false
      }, 4000)
      setTimeout(() => {
        danmakuList.value = danmakuList.value.filter(d => d.id !== dm.id)
      }, 4500)
    }
  }, 1000)
}

onMounted(() => {
  loadVideoInfo()
  startDanmaku()
})

onUnmounted(() => {
  if (danmakuTimer) {
    clearInterval(danmakuTimer)
  }
})
</script>

<style lang="scss" scoped>
.play-page {
  min-height: 100vh;
  background: #1a1a1a;
}

.play-header {
  background: #2a2a2a;
  height: 60px;
  display: flex;
  align-items: center;

  .container {
    width: 100%;
    max-width: 1600px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;

    h2 {
      color: #fff;
      font-size: 18px;
      font-weight: 500;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

.play-container {
  display: flex;
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
  gap: 20px;
}

.video-wrapper {
  flex: 1;
  position: relative;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 16 / 9;

  .video-player {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }

  .video-controls {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
    padding: 20px 16px 12px;

    .controls-bottom {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 8px;

      .controls-left {
        display: flex;
        align-items: center;
        gap: 12px;

        .time-display {
          color: #fff;
          font-size: 13px;
        }
      }
    }
  }

  .danmaku-layer {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 60px;
    pointer-events: none;
    overflow: hidden;

    .danmaku-item {
      position: absolute;
      color: #fff;
      font-size: 18px;
      text-shadow: 1px 1px 2px #000;
      white-space: nowrap;
      transition: right 4s linear;
    }
  }
}

.sidebar {
  width: 400px;
  background: #2a2a2a;
  border-radius: 8px;
  overflow: hidden;

  :deep(.el-tabs__header) {
    background: #333;
    margin: 0;
  }

  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }

  :deep(.el-tabs__item) {
    color: #999;
    height: 48px;
    line-height: 48px;

    &.is-active {
      color: #409EFF;
    }
  }
}

.chapter-list {
  max-height: 500px;
  overflow-y: auto;

  .chapter-item {
    .chapter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 14px 16px;
      background: #333;
      color: #fff;
      cursor: pointer;
      font-size: 14px;

      &.active {
        background: #409EFF;
      }
    }

    .chapter-items {
      .item-row {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 16px;
        color: #ccc;
        font-size: 13px;
        cursor: pointer;
        border-bottom: 1px solid #333;

        &.active {
          background: rgba(64, 158, 255, 0.2);
          color: #409EFF;
        }

        &.playing {
          .playing-icon {
            color: #67C23A;
          }
        }

        .item-title {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .item-duration {
          color: #666;
          font-size: 12px;
        }
      }
    }
  }
}

.comment-section {
  padding: 16px;

  .comment-input {
    margin-bottom: 20px;

    :deep(.el-textarea__inner) {
      background: #333;
      border-color: #444;
      color: #fff;
      margin-bottom: 12px;
    }
  }

  .comment-list {
    .comment-item {
      display: flex;
      gap: 12px;
      padding: 16px 0;
      border-bottom: 1px solid #333;

      .comment-body {
        flex: 1;

        .comment-header {
          display: flex;
          justify-content: space-between;
          margin-bottom: 6px;

          .user-name {
            color: #fff;
            font-weight: 600;
            font-size: 14px;
          }

          .comment-time {
            color: #666;
            font-size: 12px;
          }
        }

        .comment-content {
          color: #ccc;
          font-size: 14px;
          line-height: 1.6;
          margin-bottom: 8px;
        }
      }
    }
  }
}
</style>
