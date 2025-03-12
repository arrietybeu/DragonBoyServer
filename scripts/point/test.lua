local player = getPlayer()

if(player == nil) then
    print("Không tìm thấy player")
else
    print("Tìm thấy player")
    print("HP của Arriety là: " .. player:getPlayerPoints():getCurrentHP())
end
