function moveAway(script,sense)
	local pos=script:getPosition()
	if (pos.x<200) then

		script:reposition(pos.x+1000,pos.y+1000)
	end
end

function moveBack(script,sense)
	sense:getFlags():setFlag("boarding",0)
	local pos=script:getPosition()
	if (pos.x>200) then

		script:reposition(pos.x-1000,pos.y-1000)
	end

end

function checkBoarding(script,sense)
	boarding=sense:getFlags():readFlag("boarding")
	if (boarding>0) then
		local markTime=sense:getFlags():readFlag("CLOCK")
		local actualTime=markTime*100
		local worldTime=sense:getTime();
		if (worldTime-actualTime<1000) then
			--move away
			moveAway(script,sense)
		else
			--move back
			moveBack(script,sense)
		end
	end

end

function checkBoarded(script,sense)
	boarded=sense:getFlags():readFlag("boarded")
	if (boarded>0)then
		script:removeShip()
	end
end

function checkTime(script,sense)
	currentTime=sense:getTime()/100;
	arrivalTime=sense:getFlags():readFlag("ARRIVAL")
	if (arrivalTime==0) then
	sense:getFlags():setFlag("ARRIVAL",currentTime)	
	else 
		if (currentTime>arrivalTime+50) then
			if not (sense:getDetection():getHostile(script:getPosition().x,script:getPosition().y,8) == nil) then
			script:removeShip()
			end
		end
	end

end

function main(script,sense)  

	checkBoarding(script,sense)
	checkBoarded(script,sense)
	checkTime(script,sense)
end  