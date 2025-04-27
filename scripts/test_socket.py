import socket
import time
from concurrent.futures import ThreadPoolExecutor

HOST = '127.0.0.1'
PORT = 14445

TOTAL_CLIENTS = 1000

def client_worker(id):
    try:
        sock = socket.socket()
        sock.connect((HOST, PORT))
        print(f"✅ Client {id} đã kết nối.")

        sock.send(b'HelloServer')
        time.sleep(50) 
        sock.close()
        print(f"❌ Client {id} đã đóng kết nối.")
    except Exception as e:
        print(f"⚠️ Client {id} lỗi: {e}")

def main():
    with ThreadPoolExecutor(max_workers=1000) as executor:
        for i in range(TOTAL_CLIENTS):
            executor.submit(client_worker, i)

if __name__ == "__main__":
    main()
