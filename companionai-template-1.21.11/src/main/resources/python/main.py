import socket
import threading
import time
import logging
from logging.handlers import RotatingFileHandler

# ===================== 日志配置 =====================
# 配置日志格式：时间 - 日志级别 - 线程ID - 日志内容
LOG_FORMAT = "%(asctime)s - %(levelname)s - %(thread)d - %(message)s"
# 日志文件路径和命名
LOG_FILE = "python_server.log"

# 配置日志处理器（按大小分割，避免日志文件过大）
# 单个日志文件最大10MB，最多保留5个备份文件
file_handler = RotatingFileHandler(
    LOG_FILE,
    maxBytes=10*1024*1024,  # 10MB
    backupCount=5,
    encoding="utf-8"  # 确保中文日志正常写入
)
file_handler.setFormatter(logging.Formatter(LOG_FORMAT))

# 配置根日志器
logging.basicConfig(
    level=logging.INFO,  # 日志级别：INFO及以上都会记录
    handlers=[file_handler]  # 仅写入文件，不输出到控制台
)
logger = logging.getLogger(__name__)

# ===================== Socket 配置 =====================
HOST = '127.0.0.1'
PORT = 8888
clients = []

def handle_client(conn, addr):
    """处理客户端通信，所有输出改为日志"""
    logger.info(f"Java模组已连接：{addr}")
    clients.append(conn)
    try:
        conn.settimeout(30.0)
        while True:
            # 1. 接收Java消息
            java_msg = conn.recv(1024).decode('utf-8').strip()
            if not java_msg:
                logger.info(f"客户端{addr}主动断开连接（空消息）")
                break
            logger.info(f"收到Java消息：{java_msg}")
            
            # 2. 生成回复并发送（保留换行符关键逻辑）
            python_reply = str(time.time())
            send_data = (python_reply + "\n").encode('utf-8')
            conn.send(send_data)
            logger.info(f"✅ 已发送回复给Java：{python_reply}（带换行符）")

    except socket.timeout:
        logger.warning(f"客户端{addr}超时，断开连接")
    except Exception as e:
        logger.error(f"通信异常：{e}", exc_info=True)  # 记录异常堆栈信息
    finally:
        # 修复潜在问题：先判断conn是否在列表中，避免ValueError
        if conn in clients:
            clients.remove(conn)
        try:
            conn.close()
            logger.info(f"客户端{addr}连接已关闭")
        except Exception as e:
            logger.error(f"关闭客户端连接失败：{e}", exc_info=True)

def start_server():
    """启动Socket服务端"""
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            s.bind((HOST, PORT))
            s.listen()
            logger.info(f"Python服务端启动，监听 {HOST}:{PORT}")
            conn, addr = s.accept()
            handle_client(conn, addr)
    except Exception as e:
        logger.error(f"服务端启动/运行异常：{e}", exc_info=True)

if __name__ == "__main__":
    start_server()
