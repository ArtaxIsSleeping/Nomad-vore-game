function main(factionlibrary, player)
	flags=player:getFlags();
	var=flags:readFlag("Varless_monsterhunt1")
	if var>0 and var<4 then
		flags:incrementFlag("Varless_monsterhunt1");
	end
end