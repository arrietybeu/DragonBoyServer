import socket
import time

HOST = '127.0.0.1'
PORT = 14445

sock = socket.socket()
sock.connect((HOST, PORT))
print("✅ Đã kết nối. Giữ 10 giây...")
time.sleep(10)
sock.close()
print("❌ Đã đóng kết nối.")
