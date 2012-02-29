import sys
from java.lang 											import System
from java.util 											import Iterator
from l2.universe 										import Config
from l2.universe.gameserver.model.quest 				import State
from l2.universe.gameserver.model.quest 				import QuestState
from l2.universe.gameserver.model.quest.jython 			import QuestJython as JQuest
from l2.universe 										import L2DatabaseFactory
from l2.universe.gameserver.datatables 					import SkillTable
from l2.universe.gameserver.datatables 					import ItemTable
from l2.universe.gameserver.model.actor.instance 		import L2PcInstance
from l2.universe.gameserver.model.zone 					import L2ZoneType
from l2.universe.gameserver.network.serverpackets 		import SetupGauge

QUEST_ID = 555
QUEST_NAME   = "NPCBuffer"
QUEST_DESCRIPTION   = "custom"
QUEST_LOADING_INFO = str(QUEST_ID)+"_"+QUEST_NAME
NPC_ID = 5000

# ============================================================ #
#        GLOBAL FUNCTIONS                                      #

def getBuffType(id) : # gets buff type (depends of the ID)
	val = "none"
	conn=L2DatabaseFactory.getInstance().getConnection()
	act = conn.prepareStatement("SELECT buffType FROM buffer_buff_list WHERE buffId=? LIMIT 1")
	act.setInt(1, int(id))
	rs=act.executeQuery()
	if rs.next() :
		try :
			val = rs.getString("buffType")
		except :
			pass
	try : conn.close()
	except : pass
	return val

def isEnabled(id,level) : # check if buff is enabled
	val = "0"
	conn=L2DatabaseFactory.getInstance().getConnection()
	act = conn.prepareStatement("SELECT canUse FROM buffer_buff_list WHERE buffId=? AND buffLevel=? LIMIT 1")
	act.setInt(1, int(id))
	act.setInt(2, int(level))
	rs=act.executeQuery()
	if rs.next() :
		try :
			val = rs.getString("canUse")
		except :
			pass
	try : conn.close()
	except : pass
	if val == "1" :
		val = "True"
	elif val == "0" :
		val = "False"
	return val

def isUsed(scheme,id,level) : # check if skill is already in the scheme list
	count = 0
	used = False
	conn=L2DatabaseFactory.getInstance().getConnection()
	rss = conn.prepareStatement("SELECT COUNT(*) FROM buffer_scheme_contents WHERE scheme_id=\""+str(scheme)+"\" AND skill_id=\""+str(id)+"\" AND skill_level=\""+str(level)+"\"")
	action=rss.executeQuery()
	if action.next() :
		try :
			count = action.getInt(1);
		except :
			pass
	try : conn.close()
	except : pass
	if count > 0 :
		used = True
	else :
		used = False
	return used
	
def getVar(optionName): # gets variable from the database
	val = "0"
	conn=L2DatabaseFactory.getInstance().getConnection()
	act = conn.prepareStatement("SELECT configValue FROM buffer_configuration WHERE configName=\""+optionName+"\" LIMIT 1")
	rs=act.executeQuery()
	if rs.next() :
		try :
			val = rs.getString("configValue")
		except :
			pass
	try : conn.close()
	except : pass
	return val

#                                                              #
# ============================================================ #

def showText(type,text,buttonEnabled,buttonName,location) :
	MESSAGE = "<html><head><title>"+getVar("title")+"</title></head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>"
	MESSAGE += "<font color=\"LEVEL\">"+type+"</font><br>"+text+"<br>"
	if buttonEnabled == "True" :
		MESSAGE += "<button value=\""+buttonName+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect "+location+" 0 0\" width=100 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
	MESSAGE += "<font color=\"303030\">L2Universe</font></center></body></html>"
	return MESSAGE

def generateScheme(st) : # generates scheme list HTML: available schemes, scheme management
	schemeName = []
	schemeId = []
	HTML = ""
	conn=L2DatabaseFactory.getInstance().getConnection()
	rss = conn.prepareStatement("SELECT * FROM buffer_scheme_list WHERE player_id="+str(st.getPlayer().getObjectId()))
	action=rss.executeQuery()
	while (action.next()) :
		try :
			schemeName += [action.getString("scheme_name")]
			schemeId += [action.getString("id")]
		except :
			pass
	try : conn.close()
	except : pass
	if len(schemeName) > 0:
		HTML += "[ Available Schemes ]<br>"
		i = 0
		while i <= len(schemeName) - 1 :
			HTML += "<button value=\""+schemeName[i]+"\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" cast "+schemeId[i]+" x x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			i = i + 1
	HTML += "<br>[ Scheme Management ]<br>"
	if len(schemeName) < int(getVar("schemeCount")) :
		HTML += "<button value=\"Create Scheme\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" create_1 x x x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
	if len(schemeName) > 0 :
		HTML += "<button value=\"Edit Scheme\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" edit_1 x x x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
		HTML += "<button value=\"Delete Scheme\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" delete_1 x x x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"				
	return HTML

def	rebuildMainHtml(st) : # generating main HMTL file
	MAIN_HTML_MESSAGE = "<html><head><title>"+getVar("title")+"</title></head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>"
	available = True
	if getVar("vipBuffer") == "True" :
		if st.getPlayer().getAccessLevel() < int(getVar("vipBufferMinAccessLevel")) :
			available = False
	if available == True :		
		if st.getPlayer().isInsideZone(0) :
			if int(getVar("pvpMultiplier")) == 1 :
				MAIN_HTML_MESSAGE += "Zone price multiplier: <font color=\"LEVEL\">ON</font><br>"
			else :
				MAIN_HTML_MESSAGE += "Zone price multiplier: <font color=\"LEVEL\">"+getVar("pvpMultiplier")+"</font><br>"
		else :
			MAIN_HTML_MESSAGE += "Zone price multiplier: <font color=\"LEVEL\">ON</font><br>"		
		if getVar("schemeSystem") == "Enabled" :
			MAIN_HTML_MESSAGE += generateScheme(st) # generate the new scheme system
		if getVar("schemeSystem") == "Disabled" :
			if getVar("enableBuffs") == "True" :
				MAIN_HTML_MESSAGE += "<button value=\"Buffs\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect view_buffs 0 0\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if getVar("enableSongs") == "True" :
				MAIN_HTML_MESSAGE += "<button value=\"Songs\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect view_songs 0 0\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if getVar("enableDances") == "True" :
				MAIN_HTML_MESSAGE += "<button value=\"Dances\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect view_dances 0 0\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if getVar("enableChants") == "True" :
				MAIN_HTML_MESSAGE += "<button value=\"Chants\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect view_chants 0 0\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if getVar("enableKamael") == "True" :
				MAIN_HTML_MESSAGE += "<button value=\"Kamael\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect view_kamael 0 0\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if getVar("enableSpecial") == "True" :
				MAIN_HTML_MESSAGE += "<button value=\"Special\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect view_special 0 0\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
		if getVar("enableBuffSet") == "True" :
			MAIN_HTML_MESSAGE += "<br>[ Newbie Buff Sets ]<br>"	
			MAIN_HTML_MESSAGE += "<button value=\"Buff Set\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " castBuffSet 0 0 0\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"		
		if getVar("enableHeal") == "True" or getVar("enableBuffRemove") == "True" :
			MAIN_HTML_MESSAGE += "<br>[ Miscellaneous ]<br>"
			if getVar("enableHeal") == "True" :
				MAIN_HTML_MESSAGE += "<button value=\"Heal\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " heal 0 0 0\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"		
			if getVar("enableBuffRemove") == "True":
				MAIN_HTML_MESSAGE += "<button value=\"Remove buffs\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " removeBuffs 0 0 0\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
		if st.getPlayer().isGM() :
			MAIN_HTML_MESSAGE += "<br>[ Administration panel ]<br>"
			MAIN_HTML_MESSAGE += "<button value=\"Change Configuration\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect selectConfigSections 0 0\" width=160 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			MAIN_HTML_MESSAGE += "<button value=\"Manage Buffs\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect manage_buffs 0 0\" width=160 height=28 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"	
	else :
		MAIN_HTML_MESSAGE += "This buffer is only for VIP's!<br>Contact the administrator for more info!<br>"
	MAIN_HTML_MESSAGE += "<font color=\"303030\">L2Universe</font></center></body></html>"
	return MAIN_HTML_MESSAGE
	
class Quest (JQuest) :
	
	def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

	def onAdvEvent (self,event,npc,player) :
		st = player.getQuestState(QUEST_LOADING_INFO)
		htmltext = event
		currentTime = int(System.currentTimeMillis()/1000) # get current game time ( FOR TIME OUT SYSTEM )
		HEADER = "<html><head><title>"+getVar("title")+"</title></head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>"
		FOOTER = "<br><font color=\"303030\">L2Universe</font></center></body></html>"			
		STYLE = getVar("style")
		CONSUMABLE_ID = int(getVar("consumableId"))
		BUFF_WITH_KARMA = getVar("buffWithKarma")
		TIME_OUT = getVar("timeOut")
		TIME_OUT_TIME = int(getVar("timeOutTime"))
		FREE_BUFFS = getVar("freeBuffs")
		HEAL_PRICE = int(getVar("healPrice"))
		BUFF_PRICE = int(getVar("buffPrice"))
		SONG_PRICE = int(getVar("songPrice"))
		DANCE_PRICE = int(getVar("dancePrice"))
		CHANT_PRICE = int(getVar("chantPrice"))
		KAMAEL_PRICE = int(getVar("kamaelPrice"))
		SPECIAL_PRICE = int(getVar("specialPrice"))
		BUFF_REMOVE_PRICE = int(getVar("buffRemovePrice"))
		ENABLE_HEAL = getVar("enableHeal")
		ENABLE_BUFFS = getVar("enableBuffs")
		ENABLE_SONGS = getVar("enableSongs")
		ENABLE_DANCES = getVar("enableDances")
		ENABLE_CHANTS = getVar("enableChants")
		ENABLE_KAMAEL = getVar("enableKamael")
		ENABLE_SPECIAL = getVar("enableSpecial")
		ENABLE_BUFF_REMOVE = getVar("enableBuffRemove")
		MIN_ACCESS_LEVEL = int(getVar("gmAccessLevel"))
		ENABLE_BUFF_SET = getVar("enableBuffSet")
		BUFF_SET_PRICE = int(getVar("buffSetPrice"))
		ENABLE_BUFF_SORT = getVar("sortBuffs")
		MAX_BUFFS_PER_SCHEME = 32
		SCHEME_BUFF_PRICE = int(getVar("schemeBuffPrice"))
		SCHEMES_PER_PLAYER = getVar("schemeCount")
		PVP_ZONE_PRICE_MULTIPLIER = int(getVar("pvpMultiplier"))
		VIP_ENABLED = getVar("vipBuffer")
		VIP_MIN_ACCESS = int(getVar("vipBufferMinAccessLevel"))
		
		# ====================================================== #
		#            HTML GENERATION - SCHEME SYSTEM             #
		
		def createScheme() : # just a HTML file: scheme creation
			HTML = HEADER+"<br>You MUST seprerate new words with a dot (.)<br><br>Scheme name: <edit var=\"name\" width=100><br><br>"
			HTML += "<button value=\"Create\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" create $name no_name x x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML += "<br><button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect main 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML += FOOTER
			return HTML
			
		def deleteScheme() : # delete the scheme
			HTML = HEADER+"Available schemes:<br><br>"
			conn=L2DatabaseFactory.getInstance().getConnection()
			rss = conn.prepareStatement("SELECT * FROM buffer_scheme_list WHERE player_id="+str(st.getPlayer().getObjectId()))
			action=rss.executeQuery()
			while (action.next()) :
				try :
					HTML += "<button value=\""+action.getString("scheme_name")+"\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" delete_c "+action.getString("id")+" "+action.getString("scheme_name")+" x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
				except :
					pass
			try : conn.close()
			except : pass
			HTML += "<br><button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect main 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"			
			HTML += FOOTER
			return HTML
			
		def editScheme() : # Scheme editing
			name = ""
			id = ""
			HTML = HEADER+"Select a scheme that you would like to manage:<br><br>"
			conn=L2DatabaseFactory.getInstance().getConnection()
			rss = conn.prepareStatement("SELECT * FROM buffer_scheme_list WHERE player_id="+str(st.getPlayer().getObjectId()))
			action=rss.executeQuery()
			while (action.next()) :
				try :
					name = action.getString("scheme_name")
					id = action.getString("id")
					HTML += "<button value=\""+name+"\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" manage_scheme_select "+id+" x x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
				except :
					pass
			try : conn.close()
			except : pass
			HTML += "<br><button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect main 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML += FOOTER
			return HTML

		def getOptionList(scheme) : # Option list, when editing a scheme
			HTML = HEADER+"There are <font color=\"LEVEL\">"+str(getBuffCount(scheme))+"</font> buffs in current scheme!<br><br>"
			if getBuffCount(scheme) < MAX_BUFFS_PER_SCHEME : # if scheme still has some space
				HTML += "<button value=\"Add buffs\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" manage_scheme_1 "+str(scheme)+" 1 x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if getBuffCount(scheme) > 0 :
				HTML += "<button value=\"Remove buffs\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" manage_scheme_2 "+str(scheme)+" 1 x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"				
			HTML += "<br><button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " edit_1 0 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML += "<button value=\"Home\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect main 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML += FOOTER
			return HTML
		
		#                                                        #
		# ====================================================== #
		
		def getBuffCount(scheme) : # get buff count in the current scheme
			count = 0
			conn=L2DatabaseFactory.getInstance().getConnection()
			rss = conn.prepareStatement("SELECT COUNT(*) FROM buffer_scheme_contents WHERE scheme_id=\""+str(scheme)+"\"")
			action=rss.executeQuery()
			if action.next() :
				try :
					count = action.getInt(1)
				except :
					pass
			try : conn.close()
			except : pass
			return count
		
		def generateQuery(buff,song,dance,chant,kamael,special) :
			count = 0
			query = ""
			buffTypes = []
			if buff == "True" :
				count = count + 1
				buffTypes += ['buff']
			if song == "True" :
				count = count + 1
				buffTypes += ['song']
			if dance == "True" :
				count = count + 1
				buffTypes += ['dance']
			if chant == "True" :
				count = count + 1
				buffTypes += ['chant']
			if kamael == "True" :
				count = count + 1
				buffTypes += ['kamael']
			if special == "True" :
				count = count + 1
				buffTypes += ['special']
			i = 1
			count = count
			while i <= count :
				if i == count :
					query += buffTypes[i-1]
				else :
					query += buffTypes[i-1]+","
				i = i + 1
			return query
		
		def buildHtml(buffType): # building HTML where all buffs are shown
			HTML_MESSAGE = HEADER
			if getVar("freeBuffs") == "True" :
				HTML_MESSAGE += "All buffs are for <font color=\"LEVEL\">free</font>!<br>"			
			else :
				price = 0
				if buffType == "buff" : price = int(getVar("buffPrice"))
				if buffType == "song" : price = int(getVar("songPrice"))
				if buffType == "dance" : price = int(getVar("dancePrice"))
				if buffType == "chant" : price = int(getVar("chantPrice"))
				if buffType == "kamael" : price = int(getVar("kamaelPrice"))
				if buffType == "special" : price = int(getVar("specialPrice"))
				if player.isInsideZone(0) :
					price = price * PVP_ZONE_PRICE_MULTIPLIER
			HTML_MESSAGE += "All special buffs cost <font color=\"LEVEL\">"+str(price)+"</font> adena!<br>"
			HTML_MESSAGE += "<table>"
			conn=L2DatabaseFactory.getInstance().getConnection()
			buffCount = 0
			i = 0
			getList = conn.prepareStatement("SELECT * FROM buffer_buff_list WHERE buffType=\""+buffType+"\" AND canUse=1")
			rs=getList.executeQuery()
			while (rs.next()) :
				try :
					buffCount = buffCount + 1
				except :
					buffCount = 0
			if buffCount == 0 :
				HTML_MESSAGE += "No buffs are available at this moment!<br>"
			else :
				availableBuffs = []
				getList = conn.prepareStatement("SELECT buffId,buffLevel FROM buffer_buff_list WHERE buffType=\""+buffType+"\" AND canUse=1")
				rs=getList.executeQuery()
				while (rs.next()) :
					try :
						bId = rs.getInt("buffId")
						bLevel = rs.getInt("buffLevel")
						bName = SkillTable.getInstance().getInfo(bId,bLevel).getName()
						bName = bName.replace(" ","+")
						availableBuffs += [bName+"_"+str(bId)+"_"+str(bLevel)]
					except :
						HTML_MESSAGE += "Error loading buff list...<br>"
				availableBuffs.sort() # sorting all buffs in alphabetical order
				avBuffs = len(availableBuffs)
				format = "0000"
				for avBuffs in availableBuffs :
					buff = avBuffs
					buff = buff.replace("_"," ")
					buffSplit = buff.split(" ")
					name = buffSplit[0]
					id = int(buffSplit[1])
					level = buffSplit[2]
					name = name.replace("+"," ")
					if id < 100 :
						format = "00"+str(id)
					elif id > 99 and id < 1000 :
						format = "0"+str(id)
					else :
						format = str(id)
					i = i + 1
					if STYLE == "Icons+Buttons" :
						HTML_MESSAGE += "<tr><td><img src=\"Icon.skill"+format+"\" width=32 height=32></td><td><button value=\""+name+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " giveBuffs "+str(id)+" "+str(level)+" "+buffType+"\" width=150 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>"									
					if STYLE == "Buttons" :
						HTML_MESSAGE += "<tr><td><button value=\""+name+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " giveBuffs "+str(id)+" "+str(level)+" "+buffType+"\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>"
					if STYLE == "Text" :
						HTML_MESSAGE += "<tr><td width=150><center><a action=\"bypass -h Quest " + QUEST_LOADING_INFO + " giveBuffs "+str(id)+" "+str(level)+" "+buffType+"\">"+name+"</a></center></td></tr>"						
					if STYLE == "DualButtons" :
						if buffType == "dance" :
							name = name.replace("Concentration","Concentrat.")
						if i  == 1 :	
							HTML_MESSAGE +="<tr><td width=\"136\"><button value=\""+name+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " giveBuffs "+str(id)+" "+str(level)+" "+buffType+"\" width=136 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"
						else :
							HTML_MESSAGE +="<td width=\"136\"><button value=\""+name+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " giveBuffs "+str(id)+" "+str(level)+" "+buffType+"\" width=136 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>"
							i = 0
					if STYLE == "DualText" :
						if i == 1 :	
							HTML_MESSAGE +="<tr><td width=\"142\"><a action=\"bypass -h Quest " + QUEST_LOADING_INFO + " giveBuffs "+str(id)+" "+str(level)+" "+buffType+"\">"+name+"</a></td>"
						else :
							HTML_MESSAGE +="<td width=\"136\"><a action=\"bypass -h Quest " + QUEST_LOADING_INFO + " giveBuffs "+str(id)+" "+str(level)+" "+buffType+"\">"+name+"</a></td></tr>"
							i = 0
			try : conn.close()
			except : pass
			HTML_MESSAGE += "</table><br><button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect main 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML_MESSAGE += FOOTER
			return HTML_MESSAGE
		
		def generateQuery() :
			count = 0
			qry = ""
			buffTypes = []
			if ENABLE_BUFFS == "True" :
				count = count + 1
				buffTypes += ["\"buff\""]
			if ENABLE_SONGS == "True" :
				count = count + 1
				buffTypes += ["\"song\""]
			if ENABLE_DANCES == "True" :
				count = count + 1
				buffTypes += ["\"dance\""]
			if ENABLE_CHANTS == "True" :
				count = count + 1
				buffTypes += ["\"chant\""]
			if ENABLE_KAMAEL == "True" :
				count = count + 1
				buffTypes += ["\"kamael\""]
			if ENABLE_SPECIAL == "True" :
				count = count + 1
				buffTypes += ["\"special\""]
			aa = 1
			count = count
			while aa <= count :
				if aa == count :
					qry += buffTypes[aa-1]
				else :
					qry += buffTypes[aa-1]+","
				aa = aa + 1
			return qry
		
		def viewAllSchemeBuffs(scheme,page,action) : # buff editing
			def getBuffCount(scheme) : # get buff count in the current scheme
				count = 0
				conn=L2DatabaseFactory.getInstance().getConnection()
				rss = conn.prepareStatement("SELECT COUNT(*) FROM buffer_scheme_contents WHERE scheme_id=\""+str(scheme)+"\"")
				action=rss.executeQuery()
				if action.next() :
					try :
						count = action.getInt(1)
					except :
						pass
				try : conn.close()
				except : pass
				return count
			buffList = []
			count = 0 # buff count tracker
			pc = 0 # page count
			bll = 0
			i = 0
			buffsPerPage = 0
			incPageCount = True
			listOrder=""
			HTML_MESSAGE = HEADER
			if action == "add" :
				HTML_MESSAGE += "You can add <font color=\"LEVEL\">"+str(MAX_BUFFS_PER_SCHEME - getBuffCount(scheme))+"</font> more buffs!<br>"
				QUERY = "SELECT * FROM buffer_buff_list WHERE buffType IN ("+ generateQuery() + ") AND canUse=1"
			if action == "remove" :
				HTML_MESSAGE += "You have <font color=\"LEVEL\">"+str(getBuffCount(scheme))+"</font> buffs in this scheme!<br>"
				QUERY = "SELECT * FROM buffer_scheme_contents WHERE scheme_id="+str(scheme)
			conn=L2DatabaseFactory.getInstance().getConnection()
			getBuffCount = conn.prepareStatement(QUERY)
			rss = getBuffCount.executeQuery()
			while (rss.next()) :
				try :
					if action == "add" :
						name = SkillTable.getInstance().getInfo(rss.getInt("buffId"),rss.getInt("buffLevel")).getName()
						name = name.replace(" ","+")
						buffList += [name+"_"+str(rss.getInt("buffId"))+"_"+str(rss.getInt("buffLevel"))+"_"+str(page)]
					if action == "remove" :
						name = SkillTable.getInstance().getInfo(rss.getInt("skill_id"),rss.getInt("skill_level")).getName()
						name = name.replace(" ","+")
						buffList += [name+"_"+str(rss.getInt("skill_id"))+"_"+str(rss.getInt("skill_level"))+"_"+str(page)]						
					count = count + 1
				except :
					buffList = []
					count = 0
			try : conn.close()
			except : pass
			buffList.sort() # sorting the buffs
			HTML_MESSAGE += "<font color=\"LEVEL\">[Scheme management - Page "+str(page)+"]</font><br><table border=\"0\"><tr>"
			buffsPerPage = 25
			while incPageCount == True: # generating page count
				if count < buffsPerPage : 
					incPageCount = False
				else : 
					count = count - buffsPerPage 
				pc = pc + 1 
			ii = 1
			while ii <= pc :
				if pc > 5 :
					width = "25"
					pageName = "P"
				else :
					width = "50"
					pageName = "Page "
				if action == "add" :
					HTML_MESSAGE += "<td width=\""+width+"\"><button value=\""+pageName+""+str(ii)+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " manage_scheme_1 "+str(scheme)+" "+str(ii)+" x\" width="+width+" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"
				if action == "remove" :
					HTML_MESSAGE += "<td width=\""+width+"\"><button value=\""+pageName+""+str(ii)+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " manage_scheme_2 "+str(scheme)+" "+str(ii)+" x\" width="+width+" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"					
				ii = ii + 1
			HTML_MESSAGE += "</tr></table><br>"
			value = ""
			bll = len(buffList) # getting buff count
			j = 0
			if buffsPerPage*int(page) > bll :
				j = bll
			else :
				j = buffsPerPage*int(page)
			i = buffsPerPage*int(page)-buffsPerPage
			while i < j :
				# name_id_level_page
				value = buffList[i]
				value = value.replace("_"," ")
				extr = value.split(" ")
				name = extr[0]
				name = name.replace("+"," ")
				id = int(extr[1])
				level = extr[2]
				page = int(extr[3])
				if action == "add" :
					if isUsed(scheme,id,level) == False:
						if i % 2 != 0 :
							HTML_MESSAGE += "<table border=\"0\" bgcolor=333333>"
						else :
							HTML_MESSAGE += "<table border=\"0\" bgcolor=292929>"
						HTML_MESSAGE += "<tr><td width=\"145\">"+name+"</td><td><button value=\"Add\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" add_buff "+str(scheme)+"_"+str(id)+"_"+str(level)+" "+str(page)+" x\" width=115 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"
						HTML_MESSAGE += "</table>"
				if action == "remove" :
					if i % 2 != 0 :
						HTML_MESSAGE += "<table border=\"0\" bgcolor=333333>"
					else :
						HTML_MESSAGE += "<table border=\"0\" bgcolor=292929>"
					HTML_MESSAGE += "<tr><td width=\"145\">"+name+"</td><td><button value=\"Remove\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" remove_buff "+str(scheme)+"_"+str(id)+"_"+str(level)+" "+str(page)+" x\" width=115 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"
					HTML_MESSAGE += "</table>"
				i = i + 1 # table background tracker
			HTML_MESSAGE += "<br><br><button value=\"Back\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" manage_scheme_select "+str(scheme)+" x x\" width=115 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML_MESSAGE += "<button value=\"Home\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect main 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"				
			HTML_MESSAGE += FOOTER
			return HTML_MESSAGE

		def getAvailableSections() :
			HTML_MESSAGE = HEADER
			HTML_MESSAGE += "Select one of the sections:<br>"
			conn=L2DatabaseFactory.getInstance().getConnection()
			getList = conn.prepareStatement("SELECT * FROM buffer_config_sections ORDER BY section_id ASC")
			getConfigList = getList.executeQuery()
			while (getConfigList.next()) :
				try :
					name = getConfigList.getString("section_name")
					id = getConfigList.getString("section_id")
					HTML_MESSAGE += "<button value=\""+name+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect view_configs "+id+" 0\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
				except :
					HTML_MESSAGE += "Error loading configuration list...<br>"
					conn.close()
			try : conn.close()
			except : pass
			HTML_MESSAGE += "<br><button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect main 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML_MESSAGE += FOOTER
			return HTML_MESSAGE
			
		def buildConfigList(section): # getting all available configs
			HTML_MESSAGE = HEADER
			configCount = "0"
			conn=L2DatabaseFactory.getInstance().getConnection()
			getCount = conn.prepareStatement("SELECT COUNT(*) FROM buffer_configuration WHERE section_id=?")
			getCount.setString(1,str(section))
			act=getCount.executeQuery()
			if act.next() :
				try :
					configCount = act.getString(1)
				except :
					pass
			sectionName = "0"
			getName = conn.prepareStatement("SELECT section_name FROM buffer_config_sections WHERE section_id=?")
			getName.setString(1,str(section))
			act=getName.executeQuery()
			if act.next() :
				try :
					sectionName = act.getString("section_name")
				except :
					pass
			HTML_MESSAGE += sectionName + " Configuration - <font color=\"LEVEL\">"+configCount+"</font> configs<br>"
			getList = conn.prepareStatement("SELECT configDesc,configName,configValue FROM buffer_configuration WHERE section_id=? ORDER BY configDesc ASC")
			getList.setString(1,str(section))
			getConfigList = getList.executeQuery()
			while (getConfigList.next()) :
				try :
					desc = getConfigList.getString("configDesc")
					name = getConfigList.getString("configName")
					HTML_MESSAGE += "<button value=\""+desc+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " viewSelectedConfig "+name+" "+str(section)+" 0\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
				except :
					HTML_MESSAGE += "Error loading configuration list...<br>"
			try : conn.close()
			except : pass
			HTML_MESSAGE += "<br><button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect selectConfigSections 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML_MESSAGE += FOOTER
			return HTML_MESSAGE
		
		def viewSelectedConfig(cfgName,sectionId) : # editing selected configuration file
			HTML_MESSAGE = HEADER
			conn=L2DatabaseFactory.getInstance().getConnection()
			getVal = conn.prepareStatement("SELECT * FROM buffer_configuration WHERE configName=\""+cfgName+"\"")
			act=getVal.executeQuery()
			if act.next() :
				try :
					desc = act.getString("configDesc")
					value = act.getString("configValue")
					info = act.getString("configInfo")
					usable = act.getString("usableValues")
				except :
					usable = "none,none,none"
			else :
				desc = "---No description---"
			try : conn.close()
			except : pass
			usable = usable.replace(","," ")
			extr = usable.split(" ")
			valType = extr[0]
			minVal = extr[1]
			maxVal = extr[2]					
			HTML_MESSAGE += "Description:<br><font color=\"LEVEL\">" + desc + "</font><br>"
			HTML_MESSAGE += "Usage:<br><font color=\"LEVEL\">" + info + "</font><br>"
			HTML_MESSAGE += "Old value:<br><font color=\"LEVEL\">"
			if cfgName == "consumableId" :
				HTML_MESSAGE += ItemTable.getInstance().getTemplate(int(value)).getName() + " (ID : "+str(value)+")" # v1.2 - to make everything clear (about item ID's)
			else :
				HTML_MESSAGE += str(value)			
			HTML_MESSAGE += "</font><br>New value:  "
			if valType == "bool" :
				HTML_MESSAGE += "<combobox var=\""+cfgName+"\" width=100 List=\"True;False;\">"
			elif valType == "custom" :
				HTML_MESSAGE += "<combobox var=\""+cfgName+"\" width=110 List=\"Text;Buttons;Icons+Buttons;DualButtons;DualText\">"
			elif valType == "custom2" :
				HTML_MESSAGE += "<combobox var=\""+cfgName+"\" width=110 List=\"Disabled;Enabled\">"	
			elif valType == "string" :
				HTML_MESSAGE += "<br><multiedit var=\""+cfgName+"\" width=250 height=15>"
			else :
				HTML_MESSAGE += "<edit var=\""+cfgName+"\" width=100>"
			HTML_MESSAGE += "<br><br><button value=\"Update\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " changeConfig "+cfgName+" $"+str(cfgName)+" "+str(sectionId)+"\" width=80 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML_MESSAGE += "<button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect view_configs "+str(sectionId)+" 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML_MESSAGE += FOOTER
			return HTML_MESSAGE

		def viewAllBuffTypes() : # all available buffs
			HTML_MESSAGE = HEADER
			HTML_MESSAGE += "<font color=\"LEVEL\">[Buff management]</font><br><br>"
			if ENABLE_BUFFS == "True" :
				HTML_MESSAGE += "<button value=\"Buffs\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " edit_buff_list buff Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if ENABLE_SONGS == "True" :
				HTML_MESSAGE += "<button value=\"Songs\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " edit_buff_list song Songs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if ENABLE_DANCES == "True" :
				HTML_MESSAGE += "<button value=\"Dances\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " edit_buff_list dance Dances 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if ENABLE_CHANTS == "True" :
				HTML_MESSAGE += "<button value=\"Chants\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " edit_buff_list chant Chants 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if ENABLE_KAMAEL == "True" :
				HTML_MESSAGE += "<button value=\"Kamael Buffs\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " edit_buff_list kamael Kamael_Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			if ENABLE_SPECIAL == "True" :
				HTML_MESSAGE += "<button value=\"Special Buffs\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " edit_buff_list special Special_Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>"
			if ENABLE_BUFF_SET == "True" :
				HTML_MESSAGE += "<button value=\"Buff Sets\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " edit_buff_list set Buff_Sets 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>"			
			HTML_MESSAGE += "<button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect main 0 0\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML_MESSAGE += FOOTER
			return HTML_MESSAGE	
		
		def viewAllBuffs(type,typeName,page) : # buff editing
			buffList = []
			count = 0
			pc = 0
			bll = 0
			i = 0
			buffsPerPage = 0
			formula = 0
			incPageCount = True
			listOrder=""
			HTML_MESSAGE = HEADER
			typeName = typeName.replace("_"," ")
			if type == "set" :
				QUERY = "SELECT * FROM buffer_buff_list WHERE buffType IN ("+generateQuery()+") AND canUse=1"
			else :
				QUERY = "SELECT * FROM buffer_buff_list WHERE buffType=\""+type+"\""
			conn=L2DatabaseFactory.getInstance().getConnection()
			getBuffCount = conn.prepareStatement(QUERY)
			rss = getBuffCount.executeQuery()
			while (rss.next()) :
				try :
					name = SkillTable.getInstance().getInfo(rss.getInt("buffId"),rss.getInt("buffLevel")).getName()
					name = name.replace(" ","+")
					usable = rss.getString("canUse")
					forClass = rss.getString("forClass")
					skill_id = rss.getString("buffId")
					skill_level = rss.getString("buffLevel")
					buffList += [name+"_"+forClass+"_"+str(page)+"_"+usable+"_"+skill_id+"_"+skill_level]
					count = count + 1
				except :
					buffList = []
					count = 0
			try : conn.close()
			except : pass
			buffList.sort()
			HTML_MESSAGE += "<font color=\"LEVEL\">[Buff management - "+typeName+" - Page "+str(page)+"]</font><br><table border=\"0\"><tr>"
			if type == "set" :
				buffsPerPage = 12
			else :
				buffsPerPage = 20
			while incPageCount == True:
				if count < buffsPerPage : 
					incPageCount = False
				else : 
					count = count - buffsPerPage 
				pc = pc + 1
			ii = 1
			typeName = typeName.replace(" ","_")
			while ii <= pc :
				if pc > 5 :
					width = "25"
					pageName = "P"
				else :
					width = "50"
					pageName = "Page "
				HTML_MESSAGE += "<td width=\""+width+"\"><button value=\""+pageName+""+str(ii)+"\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " edit_buff_list "+type+" "+typeName+" "+str(ii)+"\" width="+width+" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"
				ii = ii + 1
			HTML_MESSAGE += "</tr></table><br>"
			value = ""
			bll = len(buffList)
			j = 0
			if buffsPerPage*int(page) > bll :
				j = bll
			else :
				j = buffsPerPage*int(page)
			i = buffsPerPage*int(page)-buffsPerPage
			while i < j :
				value = buffList[i]
				value = value.replace("_"," ")
				extr = value.split(" ")
				name = extr[0]
				name = name.replace("+"," ")
				forClass = int(extr[1])
				page = extr[2]
				usable = int(extr[3])
				skillPos = extr[4]+"_"+extr[5]
				if i % 2 != 0 :
					HTML_MESSAGE += "<table border=\"0\" bgcolor=333333>"
				else :
					HTML_MESSAGE += "<table border=\"0\" bgcolor=292929>"
				if type == "set" :
					if forClass == 0 :
						listOrder="List=\"Fighter;Mage;All;None;\""
					if forClass == 1 :
						listOrder="List=\"Mage;Fighter;All;None;\""
					if forClass == 2 :
						listOrder="List=\"All;Fighter;Mage;None;\""
					if forClass == 3 :
						listOrder="List=\"None;Fighter;Mage;All;\""	
					HTML_MESSAGE += "<tr><td width=\"145\">"+name+"</td><td width=\"70\"><combobox var=\"newSet"+str(i)+"\" width=70 "+listOrder+"></td>"
					HTML_MESSAGE += "<td width=\"50\"><button value=\"Update\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " changeBuffSet "+str(skillPos)+" $newSet"+str(i)+" "+page+"\" width=50 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>"
				else :
					HTML_MESSAGE += "<tr><td width=\"170\">"+name+"</td><td width=\"80\">"
					if usable == 1 :
						HTML_MESSAGE += "<button value=\"Disable\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " editSelectedBuff "+skillPos+" 0-"+page+" "+type+"\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>"
					elif usable == 0 :
						HTML_MESSAGE += "<button value=\"Enable\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " editSelectedBuff "+skillPos+" 1-"+page+" "+type+"\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>"
				HTML_MESSAGE += "</table>"
				i = i + 1
			HTML_MESSAGE += "<br><br><button value=\"Back\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect manage_buffs 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML_MESSAGE += "<button value=\"Home\" action=\"bypass -h Quest " + QUEST_LOADING_INFO + " redirect main 0 0\" width=150 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"				
			HTML_MESSAGE += FOOTER
			return HTML_MESSAGE	

		def manageSelectedBuff(buffPosId,canUseBuff) :
			bpid = buffPosId.split("_")
			bId= bpid[0]
			bLvl= bpid[1]
			conn=L2DatabaseFactory.getInstance().getConnection()
			upd=conn.prepareStatement("UPDATE buffer_buff_list SET canUse=\""+canUseBuff+"\" WHERE buffId=\""+str(bId)+"\" AND buffLevel=\""+str(bLvl)+"\" LIMIT 1")
			try :
				upd.executeUpdate()
				upd.close()
			except :
				pass
			try : conn.close()
			except : pass

		def manageSelectedSet(id,newVal,opt3) : # Buff set management...
			bpid = id.split("_")
			bId= bpid[0]
			bLvl= bpid[1]		
			conn=L2DatabaseFactory.getInstance().getConnection()
			upd=conn.prepareStatement("UPDATE buffer_buff_list SET forClass=? WHERE buffId=? AND bufflevel=?")
			upd.setString(1, newVal)
			upd.setString(2, str(bId))
			upd.setString(3, str(bLvl))
			try :
				upd.executeUpdate()
				upd.close()
			except :
				pass
			try : conn.close()
			except : pass
			return viewAllBuffs("set","Buff Sets",str(opt3))

		# v1.1 update:
		# added security check of the new value, so the player can't mess up the database
		def updateConfigValue(configName,newValue,sectionId) :
			conn=L2DatabaseFactory.getInstance().getConnection()
			getVal = conn.prepareStatement("SELECT usableValues FROM buffer_configuration WHERE configName=\""+configName+"\"")
			act=getVal.executeQuery()
			if act.next() :
				try :
					usable = act.getString("usableValues")
				except :
					usable = "none,none,none"
			usable = usable.replace(","," ")
			extr = usable.split(" ")
			valType = extr[0]
			minVal = extr[1]
			maxVal = extr[2]
			returnText = ""
			if newValue == "" :
				returnText = showText("Info","You must enter a new value!","True","Return","view_configs "+str(sectionId))
			else :
				if valType == "bool" :
					upd=conn.prepareStatement("UPDATE buffer_configuration SET configValue=\""+newValue+"\" WHERE configName=\""+configName+"\"")
					try :
						upd.executeUpdate()
						upd.close()
						configs = ["enableBuffs","enableSongs","enableDances","enableChants","enableKamael","enableSpecial"]
						
						if configName in configs :
							upd2=conn.prepareStatement("UPDATE buffer_scheme_list SET mod_accepted=1")
							try :
								upd2.executeUpdate()
							except:
								pass
						returnText = showText("Info","Value has been changed successfully!","True","Return","view_configs "+str(sectionId))
					except:
						pass
				else :
					if valType == "range" :
						if newValue.isdigit() and int(newValue) >= int(minVal) and int(newValue) <= int(maxVal) :
							upd=conn.prepareStatement("UPDATE buffer_configuration SET configValue=\""+newValue+"\" WHERE configName=\""+configName+"\"")
							try :
								upd.executeUpdate()
								upd.close()
								returnText = showText("Info","Value has been changed successfully!","True","Return","view_configs "+str(sectionId))
							except:
								pass
						else :
							returnText = showText("Info","You must enter an integer from "+minVal+" to "+maxVal+"!","True","Return","view_configs "+str(sectionId))
					else :
						if valType == "string" :
							newValue = newValue.replace(","," ")
							if len(newValue) < int(minVal) or len(newValue) > int(maxVal) :
								returnText = showText("Info","You must enter a value that:<br>   1) Isn't shorter than 3 characters!<br>   2) Isn't longer than 36 characters!","True","Return","view_configs")
							else :
								upd=conn.prepareStatement("UPDATE buffer_configuration SET configValue=? WHERE configName=\""+configName+"\"")
								upd.setString(1, newValue)
								try :
									upd.executeUpdate()
									upd.close()
									returnText = showText("Info","Value has been changed successfully!","True","Return","view_configs "+str(sectionId))
								except:
									pass
						else :
							if valType == "custom" :
								upd=conn.prepareStatement("UPDATE buffer_configuration SET configValue=? WHERE configName=\""+configName+"\"")
								upd.setString(1, newValue)
								try :
									upd.executeUpdate()
									upd.close()
									returnText = showText("Info","Value has been changed successfully!","True","Return","view_configs "+str(sectionId))
								except:
									pass
							if valType == "custom2" :
								upd=conn.prepareStatement("UPDATE buffer_configuration SET configValue=? WHERE configName=\""+configName+"\"")
								upd.setString(1, newValue)
								try :
									upd.executeUpdate()
									upd.close()
									returnText = showText("Info","Value has been changed successfully!","True","Return","view_configs "+str(sectionId))
								except:
									pass
			try : conn.close()
			except : pass
			return returnText

		def addTimeout(gaugeColor,amount,offset) :
			endtime = currentTime + amount
			st.set("blockUntilTime",str(endtime))
			st.getPlayer().sendPacket(SetupGauge(gaugeColor, amount * 1000 + offset))	
			
		def heal() :
			st.getPlayer().getStatus().setCurrentHp(st.getPlayer().getStat().getMaxHp())
			st.getPlayer().getStatus().setCurrentMp(st.getPlayer().getStat().getMaxMp())
			st.getPlayer().getStatus().setCurrentCp(st.getPlayer().getStat().getMaxCp())			
		
		# HTML HANDLING, BUFF USING, CONFIGURATION MODIFYING			
		# splitting all values from HTML
		eventSplit = event.split(" ")
		event = eventSplit[0]
		eventParam1 = eventSplit[1]
		eventParam2 = eventSplit[2]
		eventParam3 = eventSplit[3]
		
		if event == "redirect" :
			if eventParam1 == "main" :
				return rebuildMainHtml(st)
			if eventParam1 == "view_configs" :
				return buildConfigList(eventParam2)
			if eventParam1 == "manage_buffs" :
				return viewAllBuffTypes()
			if eventParam1 == "selectConfigSections" :
				return getAvailableSections()
		
		# SCHEME SYSTEM
				
		if event == "create" :
			param = eventParam1.replace("."," ")
			if param == "no_name" :
				return showText("Info","Please, enter the scheme name!","True","Return","main")
			else :
				con=L2DatabaseFactory.getInstance().getConnection()
				ins = con.prepareStatement("INSERT INTO buffer_scheme_list (player_id,scheme_name) VALUES (?,?)")
				ins.setString(1, str(player.getObjectId()))
				ins.setString(2, param)
				try :
					ins.executeUpdate()
					ins.close()
				except :
					pass
				try : con.close()
				except : pass
			return rebuildMainHtml(st)
			
		if event == "delete" :
			conn=L2DatabaseFactory.getInstance().getConnection()
			rem=conn.prepareStatement("DELETE FROM buffer_scheme_list WHERE id=? LIMIT 1")
			rem.setString(1, eventParam1)
			try :
				rem.executeUpdate()
			except :
				pass
			rem=conn.prepareStatement("DELETE FROM buffer_scheme_contents WHERE scheme_id=?")
			rem.setString(1, eventParam1)
			try :
				rem.executeUpdate()
				rem.close()
			except :
				pass
			try : conn.close()
			except : pass
			return rebuildMainHtml(st)
		
		if event == "delete_c" :
			HTML = HTML_MESSAGE = HEADER+"Do you really want to delete '"+eventParam2+"' scheme?<br><br>"
			HTML += "<button value=\"Yes\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" delete "+eventParam1+" x x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			HTML += "<button value=\"No\" action=\"bypass -h Quest "+QUEST_LOADING_INFO+" delete_1 x x x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"				
			HTML += FOOTER
			return HTML			
		
		if event == "create_1" :
			return createScheme()
			
		if event == "edit_1" :
			return editScheme()
		
		if event == "delete_1" :
			return deleteScheme()
		
		if event == "manage_scheme_1" :
			return viewAllSchemeBuffs(eventParam1,eventParam2,"add")

		if event == "manage_scheme_2" :
			return viewAllSchemeBuffs(eventParam1,eventParam2,"remove")
			
		if event == "manage_scheme_select" :
			return getOptionList(eventParam1)
		
		if event == "remove_buff" :
			event = eventParam1.split("_")
			scheme = event[0]
			skill = event[1]
			level = event[2]
			con=L2DatabaseFactory.getInstance().getConnection()
			rem=con.prepareStatement("DELETE FROM buffer_scheme_contents WHERE scheme_id=? AND skill_id=? AND skill_level=? LIMIT 1")
			rem.setString(1, scheme)
			rem.setString(2, skill)
			rem.setString(3, level)
			try :
				rem.executeUpdate()
			except :
				pass
			try : con.close()
			except : pass
			if getBuffCount(scheme) == 0 :
				HTML = getOptionList(scheme)
			else :
				HTML = viewAllSchemeBuffs(scheme,eventParam2,"remove")
			return HTML
			
		if event == "add_buff" :
			event = eventParam1.split("_")
			scheme = event[0]
			skill = event[1]
			level = event[2]
			con=L2DatabaseFactory.getInstance().getConnection()
			ins = con.prepareStatement("INSERT INTO buffer_scheme_contents (scheme_id,skill_id,skill_level) VALUES (?,?,?)")
			ins.setString(1, str(scheme))
			ins.setString(2, str(skill))
			ins.setString(3, str(level))
			try :
				ins.executeUpdate()
				ins.close()
			except :
				pass
			try : con.close()
			except : pass
			if getBuffCount(scheme) == MAX_BUFFS_PER_SCHEME :
				HTML = getOptionList(scheme)
			else :
				HTML = viewAllSchemeBuffs(scheme,eventParam2,"add")
			return HTML
		
		if event == "edit_buff_list" :
			return viewAllBuffs(eventParam1,eventParam2,eventParam3)
		
		if event == "changeBuffSet" :
			eventParam2 = eventParam2.replace("Fighter","0")
			eventParam2 = eventParam2.replace("Mage","1")
			eventParam2 = eventParam2.replace("All","2")
			eventParam2 = eventParam2.replace("None","3")
			return manageSelectedSet(eventParam1,eventParam2,eventParam3)
				
		if event == "editSelectedBuff" :
			eventParam2 = eventParam2.replace("-"," ")
			split = eventParam2.split(" ")
			action = split[0]
			page = split[1]		
			manageSelectedBuff(eventParam1,action)
			if eventParam3 == "buff" : typeName = "Buffs"
			if eventParam3 == "song" : typeName = "Songs"
			if eventParam3 == "dance" : typeName = "Dances"
			if eventParam3 == "chant" : typeName = "Chants"
			if eventParam3 == "kamael" : typeName = "Kamael_Buffs"
			if eventParam3 == "special" : typeName = "Special_Buffs"
			return viewAllBuffs(eventParam3,typeName,page)
				
		if event == "viewSelectedConfig" :
			return viewSelectedConfig(eventParam1,eventParam2)
					
		if event == "changeConfig" :
			return updateConfigValue(eventParam1,eventParam2,eventParam3)
		
		if event == "redirect" :
			if eventParam1 == "view_buffs" :
				return buildHtml("buff")
			if eventParam1 == "view_songs" :
				return buildHtml("song")
			if eventParam1 == "view_dances" :
				return buildHtml("dance")
			if eventParam1 == "view_chants" :
				return buildHtml("chant")
			if eventParam1 == "view_kamael" :
				return buildHtml("kamael")
			if eventParam1 == "view_special" :
				return buildHtml("special")
		# buff casting code
		if event == "heal" :
			if currentTime > st.getInt("blockUntilTime") :
				if player.isInsideZone(0) :
					HEAL_PRICE = HEAL_PRICE * PVP_ZONE_PRICE_MULTIPLIER
				if st.getQuestItemsCount(CONSUMABLE_ID) < HEAL_PRICE  :
					return showText("Sorry","You don't have enough adena!","False",0,0)
				else :
					heal()					
					st.takeItems(CONSUMABLE_ID,HEAL_PRICE)					
					if TIME_OUT == "True":
						addTimeout(1,TIME_OUT_TIME + 2,600)
					return rebuildMainHtml(st)
			return rebuildMainHtml(st)
			
		if event == "removeBuffs" :
			if currentTime > st.getInt("blockUntilTime") :		
				if player.isInsideZone(0) :
					BUFF_REMOVE_PRICE = BUFF_REMOVE_PRICE * PVP_ZONE_PRICE_MULTIPLIER				
				if st.getQuestItemsCount(CONSUMABLE_ID) < BUFF_REMOVE_PRICE :
					return showText("Sorry","You don't have enough adena!","False",0,0)
				else :
					st.getPlayer().stopAllEffects()
					st.takeItems(CONSUMABLE_ID,BUFF_REMOVE_PRICE)
					if TIME_OUT == "True":
						addTimeout(2,TIME_OUT_TIME + 3,600)
					return rebuildMainHtml(st)
			return rebuildMainHtml(st)

		if event == "cast" :
			if currentTime > st.getInt("blockUntilTime") :
				buffs = []
				levels = []
				id = 0
				level = 0
				conn=L2DatabaseFactory.getInstance().getConnection()
				rss = conn.prepareStatement("SELECT * FROM buffer_scheme_contents WHERE scheme_id="+eventParam1)
				action=rss.executeQuery()
				while (action.next()) :
					try :
						enabled = 1
						# check if that buff is enabled
						id = int(action.getString("skill_id"))
						level = int(action.getString("skill_level"))
						skillType = getBuffType(int(action.getString("skill_id")))
						if skillType == "buff" :
							if ENABLE_BUFFS == "True" :
								if isEnabled(id,level) == "True" :
									buffs += [id]
									levels += [level]
						if skillType == "song" :
							if ENABLE_SONGS == "True" :
								if isEnabled(id,level) == "True" :
									buffs += [id]
									levels += [level]
						if skillType == "dance" :
							if ENABLE_DANCES == "True" :
								if isEnabled(id,level) == "True" :
									buffs += [id]
									levels += [level]
						if skillType == "chant" :
							if ENABLE_CHANTS == "True" :
								if isEnabled(id,level) == "True" :
									buffs += [id]
									levels += [level]
						if skillType == "kamael" :
							if ENABLE_KAMAEL == "True" :
								if isEnabled(id,level) == "True" :
									buffs += [id]
									levels += [level]
						if skillType == "special" :
							if ENABLE_SPECIAL == "True" :
								if isEnabled(id,level) == "True" :
									buffs += [id]
									levels += [level]
					except :
						print "Query error!"
				try : conn.close()
				except : pass
					
				# check if there are any buffs in the array
				if len(buffs) == 0 :
					return viewAllSchemeBuffs(eventParam1,1,"add")
				# clear till this point
				else :
					if getVar("freeBuffs") == "False" :
						if player.isInsideZone(0) :
							SCHEME_BUFF_PRICE = SCHEME_BUFF_PRICE * PVP_ZONE_PRICE_MULTIPLIER
						if st.getQuestItemsCount(CONSUMABLE_ID) < SCHEME_BUFF_PRICE :
							return showText("Sorry","You don't have enough adena!","False",0,0)
						else :
							st.takeItems(CONSUMABLE_ID,SCHEME_BUFF_PRICE)
					i = 0
					while i <= len(buffs) - 1 :
						SkillTable.getInstance().getInfo(buffs[i],levels[i]).getEffects(player,player)
						i = i + 1
					heal()
					if getVar("timeOut") == "True":
						addTimeout(3,TIME_OUT_TIME+5,600)
					return rebuildMainHtml(st)
			else :
				return rebuildMainHtml(st)

		if event == "giveBuffs" :
			if eventParam3 == "buff" :
				cost = BUFF_PRICE
			if eventParam3 == "song" :
				cost = SONG_PRICE
			if eventParam3 == "dance" :
				cost = DANCE_PRICE
			if eventParam3 == "chant" :
				cost = CHANT_PRICE
			if eventParam3 == "kamael" :
				cost = KAMAEL_PRICE
			if eventParam3 == "special" :
				cost = SPECIAL_PRICE				
			if currentTime > st.getInt("blockUntilTime") :
				if getVar("freeBuffs") == "False" :
					# we need to generate the price depending of the Zone type
					if player.isInsideZone(0) :
						cost = cost * PVP_ZONE_PRICE_MULTIPLIER
					if st.getQuestItemsCount(CONSUMABLE_ID) < cost :
						return showText("Sorry","You don't have enough adena!","False",0,0)
					else :
						st.takeItems(CONSUMABLE_ID,cost)
				# maybe need some space check..
				SkillTable.getInstance().getInfo(int(eventParam1),int(eventParam2)).getEffects(st.getPlayer(),st.getPlayer())
				heal()
				if getVar("timeOut") == "True":
					addTimeout(3,TIME_OUT_TIME,600)
				return buildHtml(eventParam3)
			else :
				return buildHtml(eventParam3)
				
		if event == "castBuffSet" :
			if currentTime > st.getInt("blockUntilTime") :
				if getVar("freeBuffs") == "False" :
					if player.isInsideZone(0) :
						BUFF_SET_PRICE = BUFF_SET_PRICE * PVP_ZONE_PRICE_MULTIPLIER
					if st.getQuestItemsCount(CONSUMABLE_ID) < BUFF_SET_PRICE :
						return showText("Sorry","You don't have enough adena!","False",0,0)
					else :
						st.takeItems(CONSUMABLE_ID,BUFF_SET_PRICE)
				buff_sets=[]
				player_class = 3
				i = 0
				if st.getPlayer().isMageClass() :
					player_class = 1
				else :
					player_class = 0
				conn=L2DatabaseFactory.getInstance().getConnection()
				getSimilarNameCount = conn.prepareStatement("SELECT buffId,buffLevel FROM buffer_buff_list WHERE forClass IN (?,?) ORDER BY id ASC")
				getSimilarNameCount.setString(1, str(player_class))
				getSimilarNameCount.setString(2, "2")
				rss = getSimilarNameCount.executeQuery()
				while (rss.next()) :
					try :
						id = rss.getInt("buffId")
						lvl = rss.getInt("buffLevel")
						buff_sets += [id,lvl]
					except :
						buff_sets = []
				try : conn.close()
				except : pass
				while i <= len(buff_sets)-2 :
					SkillTable.getInstance().getInfo(buff_sets[i],buff_sets[i+1]).getEffects(st.getPlayer(),st.getPlayer())
					i = i + 2
				heal()
				if getVar("timeOut") == "True":
					addTimeout(3,TIME_OUT_TIME + 10,600)
				return rebuildMainHtml(st)
			else :
				return rebuildMainHtml(st)
				
		return rebuildMainHtml(st)
							
	def onFirstTalk (self,npc,player):
		st = player.getQuestState(QUEST_LOADING_INFO)
		if not st :
			st = self.newQuestState(player)
		if player.isGM() :
			return rebuildMainHtml(st)
		else :
			if player.getLevel() < int(getVar("minlevel")) :
				return showText("Info","Your level is too low!<br>You have to be at least level <font color\"LEVEL\">"+getVar("minlevel")+"</font>,<br>to use my services!","False","Return","main")
			elif getVar("buffWithKarma") == "False" and player.getKarma() > 0 :
				return showText("Info","You have too much karma!<br>Come back,<br>when you don't have any karma!","False","Return","main")
			elif player.getPvpFlag() > 0 :
				return showText("Info","You can't buff while you are flagged!<br>Wait some time and try again!","False","Return","main")
			elif player.isAttackingNow() :
				return showText("Info","You can't buff while you are attacking!<br>Stop your fight and try again!","False","Return","main")
			else:
				return rebuildMainHtml(st)

		
QUEST = Quest(QUEST_ID,QUEST_LOADING_INFO,QUEST_DESCRIPTION)

QUEST.addStartNpc(NPC_ID)
QUEST.addFirstTalkId(NPC_ID)
QUEST.addTalkId(NPC_ID)