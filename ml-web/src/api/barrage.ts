class BarrageService {
  private ws: WebSocket | null = null
  private userId: number = 0
  private messageHandlers: ((data: any) => void)[] = []
  private reconnectTimer: number | null = null
  private readonly MAX_RECONNECT_ATTEMPTS = 5
  private reconnectAttempts = 0

  connect(userId: number): Promise<void> {
    return new Promise((resolve, reject) => {
      this.userId = userId
      const wsUrl = `ws://localhost:24106/api/v1/barrage/${userId}`
      
      try {
        this.ws = new WebSocket(wsUrl)
        
        this.ws.onopen = () => {
          console.log('弹幕 WebSocket 连接成功')
          this.reconnectAttempts = 0
          resolve()
        }
        
        this.ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data)
            this.messageHandlers.forEach(handler => handler(data))
          } catch (error) {
            console.error('解析弹幕消息失败', error)
          }
        }
        
        this.ws.onerror = (error) => {
          console.error('弹幕 WebSocket 错误', error)
          reject(error)
        }
        
        this.ws.onclose = () => {
          console.log('弹幕 WebSocket 连接关闭')
          this.ws = null
          this.attemptReconnect()
        }
      } catch (error) {
        reject(error)
      }
    })
  }

  private attemptReconnect() {
    if (this.reconnectAttempts < this.MAX_RECONNECT_ATTEMPTS) {
      this.reconnectAttempts++
      console.log(`尝试重连弹幕服务 (${this.reconnectAttempts}/${this.MAX_RECONNECT_ATTEMPTS})`)
      this.reconnectTimer = window.setTimeout(() => {
        this.connect(this.userId).catch(console.error)
      }, 3000)
    } else {
      console.error('弹幕服务重连失败，已达到最大重试次数')
    }
  }

  send(message: { episodeId: number; content: string; time: number }): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message))
    } else {
      console.error('弹幕 WebSocket 未连接')
      throw new Error('弹幕服务未连接')
    }
  }

  onMessage(handler: (data: any) => void): void {
    this.messageHandlers.push(handler)
  }

  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    this.messageHandlers = []
  }
}

export const barrageService = new BarrageService()
