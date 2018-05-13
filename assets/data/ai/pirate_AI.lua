
function move(controllable,sense,script)
	x=script:getShared():getValue(0)
	y=script:getShared():getValue(1)
	if (x>0) then
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(x,y,4)
		end		
	else
		a=math.random(0,8)
		if (a<8) then
			controllable:move(a);
		end
	end
end

function combat(controllable,script,hostile,pos)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
		controllable:setAttack(0)
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else
		a=controllable:getValue(2)
		if (a>=2) then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
			end
		else
			a=a+1
			controllable:setValue(2,a)
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
		
		end
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
	--combat ai here
	combat(controllable,script,hostile,pos)
	script:getShared():setValue(0,hostile:getPosition().x)
	script:getShared():setValue(1,hostile:getPosition().y)
	script:getShared():setValue(7,10)
	else
	move(controllable,sense,script)
	end
end  