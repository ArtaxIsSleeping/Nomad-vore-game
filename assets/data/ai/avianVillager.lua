
function combat(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		end
	end

end

function move(controllable,sense,pos)
	
	if (sense:CanWalk(pos.x, pos.y)) then
		b=math.random(0,16)
		if (b>14) then
			a=math.random(0,8)
			controllable:move(a);		
		end	
	else
		a=math.random(0,8)
		controllable:move(a);
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:isPeace() then
	--combat ai here
	combat(controllable,sense,pos,hostile)

	else
		move(controllable,sense,pos)
	end
end  