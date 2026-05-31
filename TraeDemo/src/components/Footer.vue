<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-left">
          <div class="footer-logo">
            <span>在线课堂</span>
          </div>
          <p class="footer-desc">专业的在线教育平台，提供优质的课程内容</p>
        </div>
        <div class="footer-links">
          <div class="link-group">
            <h4>关于我们</h4>
            <a href="#">平台介绍</a>
            <a href="#">联系我们</a>
            <a href="#">帮助中心</a>
          </div>
          <div class="link-group">
            <h4>学习指南</h4>
            <a href="#">如何注册</a>
            <a href="#">如何选课</a>
            <a href="#">常见问题</a>
          </div>
          <div class="link-group">
            <h4>合作加盟</h4>
            <a href="#">机构入驻</a>
            <a href="#">推广合作</a>
            <a href="#">内容合作</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 在线课堂平台. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<style lang="scss" scoped>
.footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 40px 0 20px;
  margin-top: 60px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  padding-bottom: 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-left {
  .footer-logo {
    font-size: 20px;
    font-weight: 600;
    color: #409EFF;
    margin-bottom: 12px;
  }

  .footer-desc {
    font-size: 14px;
    color: #95a5a6;
  }
}

.footer-links {
  display: flex;
  gap: 80px;

  .link-group {
    h4 {
      font-size: 16px;
      margin-bottom: 16px;
      color: #fff;
    }

    a {
      display: block;
      font-size: 14px;
      color: #95a5a6;
      margin-bottom: 10px;
      transition: color 0.3s;

      &:hover {
        color: #409EFF;
      }
    }
  }
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  font-size: 13px;
  color: #7f8c8d;
}
</style>
