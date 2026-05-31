// 环境IP地址
const HOST = 'http://localhost';
export const GATEWAY_HOST = `${HOST}:24101`;
export const USER_EXCEL_HOST = `${GATEWAY_HOST}/user-server/api/v1/user/excel`;
export const EPISODE_EXCEL_HOST = `${GATEWAY_HOST}/course-server/v1/episode/excel`;
export const ORDER_EXCEL_HOST = `${GATEWAY_HOST}/order-server/api/v1/order/excel`;

// Minio函数
export const MINIO_HOST = `http://192.168.227.128:9001/mylesson`;
export const MINIO_AVATAR = (url: string) => MINIO_HOST + '/avatar/' + url;
export const MINIO_BANNER = (url: string) => MINIO_HOST + '/banner/' + url;
export const MINIO_COURSE_COVER = (url: string) => MINIO_HOST + '/course-cover/' + url;
export const MINIO_COURSE_SUMMARY = (url: string) => MINIO_HOST + '/course-summary/' + url;
export const MINIO_EPISODE_VIDEO = (url: string) => MINIO_HOST + '/episode-video/' + url;
export const MINIO_EPISODE_VIDEO_COVER = (url: string) => MINIO_HOST + '/episode-video-cover/' + url;