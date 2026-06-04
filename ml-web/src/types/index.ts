export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  phone: string
  signature: string
  role: string
  createTime: string
}

export interface Category {
  id: number
  name: string
  icon: string
  sort: number
}

export interface Course {
  id: number
  title: string
  cover: string
  price: number
  originalPrice: number
  info: string
  categoryId: number
  author: string
  studentCount: number
  episodeCount: number
  status: number
}

export interface Season {
  id: number
  courseId: number
  title: string
  sort: number
  episodes: Episode[]
}

export interface Episode {
  id: number
  seasonId: number
  title: string
  duration: number
  videoUrl: string
  sort: number
  free: boolean
}

export interface Comment {
  id: number
  courseId: number
  fkUserId: number
  nickname: string
  avatar: string
  content: string
  likeCount: number
  liked: boolean
  createTime: string
}

export interface CartItem {
  id: number
  courseId: number
  courseTitle: string
  courseCover: string
  coursePrice: number
  quantity: number
}

export interface Order {
  id: number
  orderNo: string
  totalAmount: number
  status: number
  statusText: string
  createTime: string
  items: OrderItem[]
  
  sn: string
  payAmount: number
}

export interface OrderItem {
  courseId: number
  courseTitle: string
  courseCover: string
  price: number
}

export interface Coupon {
  id: number
  name: string
  amount: number
  condition: string
  startTime: string
  endTime: string
  received: boolean
}

export interface SeckillDetail {
  id: number
  fkSeckillId: number//秒杀活动id
  fkCourseId: number// 课程id
  fkUserId: number//用户id
  courseTitle: string//课程标题
  courseCover: string//课程封面
  coursePrice: number//课程原价
  skPrice: number//秒杀价
  stock: number//秒杀数量
}

export interface SeckillActivity {
  id: number
  title: string
  cover: string
  price: number
  originalPrice: number
  stock: number
  startTime: string
  endTime: string
  seckillDetails: SeckillDetail[]
}

export interface Banner {
  id: number
  image: string
  url: string
  title: string
  sort: number
}

export interface Notice {
  id: number
  title: string
  content: string
  createTime: string
  type: string
}

export interface Danmaku {
  id: number
  episodeId: number
  userId: number
  username: string
  content: string
  time: number
  createTime: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}