import os

folder_path = r"E:\Project\Client\KnightAge\Assets"  
extension = ".asset"  # Phần mở rộng file muốn xóa

for root, dirs, files in os.walk(folder_path):
    for file in files:
        if file.endswith(extension):
            os.remove(os.path.join(root, file))
            print(f"Remove to: {os.path.join(root, file)}")

print("Hoan Thanh!")
