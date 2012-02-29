#!/bin/bash
############################################
## WARNING!  WARNING!  WARNING!  WARNING! ##
##                                        ##
## DON'T USE NOTEPAD TO CHANGE THIS FILE  ##
## INSTEAD USE SOME DECENT TEXT EDITOR.   ##
## NEWLINE CHARACTERS DIFFER BETWEEN DOS/ ##
## WINDOWS AND UNIX.                      ##
##                                        ##
## USING NOTEPAD TO SAVE THIS FILE WILL   ##
## LEAVE IT IN A BROKEN STATE!!!          ##
############################################
## Writen by DrLecter                     ##
## License: GNU GPL                       ##
## Based on Tiago Tagliaferri's script    ##
## E-mail: tiago_tagliaferri@msn.com      ##
## From "l2universe"                     ##
############################################
trap finish 2

configure() {
echo "#############################################"
echo "# You entered script configuration area     #"
echo "# No change will be performed in your DB    #"
echo "# I will just ask you some questions about  #"
echo "# your hosts and DB.                        #"
echo "#############################################"
MYSQLDUMPPATH=`which -a mysqldump 2>/dev/null`
MYSQLPATH=`which -a mysql 2>/dev/null`
if [ $? -ne 0 ]; then
echo "We were unable to find MySQL binaries on your path"
while :
 do
  echo -ne "\nPlease enter MySQL binaries directory (no trailing slash): "
  read MYSQLBINPATH
    if [ -e "$MYSQLBINPATH" ] && [ -d "$MYSQLBINPATH" ] && \
       [ -e "$MYSQLBINPATH/mysqldump" ] && [ -e "$MYSQLBINPATH/mysql" ]; then
       MYSQLDUMPPATH="$MYSQLBINPATH/mysqldump"
       MYSQLPATH="$MYSQLBINPATH/mysql"
       break
    else
       echo "The data you entered is invalid. Please verify and try again."
       exit 1
    fi
 done
fi
#LS
echo -ne "\nPlease enter MySQL Login Server hostname (default localhost): "
read LSDBHOST
if [ -z "$LSDBHOST" ]; then
  LSDBHOST="localhost"
fi
echo -ne "\nPlease enter MySQL Login Server database name (default l2universe): "
read LSDB
if [ -z "$LSDB" ]; then
  LSDB="l2universe"
fi
echo -ne "\nPlease enter MySQL Login Server user (default root): "
read LSUSER
if [ -z "$LSUSER" ]; then
  LSUSER="root"
fi
echo -ne "\nPlease enter MySQL Login Server $LSUSER's password (won't be displayed) :"
stty -echo
read LSPASS
stty echo
echo ""
if [ -z "$LSPASS" ]; then
  echo "Hum.. I'll let it be but don't be stupid and avoid empty passwords"
elif [ "$LSUSER" == "$LSPASS" ]; then
  echo "You're not too brilliant choosing passwords huh?"
fi
#CB
echo -ne "\nPlease enter MySQL Community Server hostname (default localhost): "
read CBDBHOST
if [ -z "$CBDBHOST" ]; then
  CBDBHOST="localhost"
fi
echo -ne "\nPlease enter MySQL Community Server database name (default l2universecb): "
read CBDB
if [ -z "$CBDB" ]; then
  CBDB="l2universecb"
fi
echo -ne "\nPlease enter MySQL Community Server user (default root): "
read CBUSER
if [ -z "$CBUSER" ]; then
  CBUSER="root"
fi
echo -ne "\nPlease enter MySQL Community Server $CBUSER's password (won't be displayed) :"
stty -echo
read CBPASS
stty echo
echo ""
if [ -z "$CBPASS" ]; then
  echo "Hum.. I'll let it be but don't be stupid and avoid empty passwords"
elif [ "$CBUSER" == "$CBPASS" ]; then
  echo "You're not too brilliant choosing passwords huh?"
fi
#GS
echo -ne "\nPlease enter MySQL Game Server hostname (default $LSDBHOST): "
read GSDBHOST
if [ -z "$GSDBHOST" ]; then
  GSDBHOST="$LSDBHOST"
fi
echo -ne "\nPlease enter MySQL Game Server database name (default $LSDB): "
read GSDB
if [ -z "$GSDB" ]; then
  GSDB="$LSDB"
fi
echo -ne "\nPlease enter MySQL Game Server user (default $LSUSER): "
read GSUSER
if [ -z "$GSUSER" ]; then
  GSUSER="$LSUSER"
fi
echo -ne "\nPlease enter MySQL Game Server $GSUSER's password (won't be displayed): "
stty -echo
read GSPASS
stty echo
echo ""
if [ -z "$GSPASS" ]; then
  echo "Hum.. I'll let it be but don't be stupid and avoid empty passwords"
elif [ "$GSUSER" == "$GSPASS" ]; then
  echo "You're not too brilliant choosing passwords huh?"
fi
save_config $1
}

save_config() {
if [ -n "$1" ]; then
CONF="$1"
else 
CONF="database_installer.rc"
fi
echo ""
echo "With these data I can generate a configuration file which can be read"
echo "on future updates. WARNING: this file will contain clear text passwords!"
echo -ne "Shall I generate config file $CONF? (Y/n):"
read SAVE
if [ "$SAVE" == "y" -o "$SAVE" == "Y" -o "$SAVE" == "" ];then 
cat <<EOF>$CONF
#Configuration settings for l2universe database installer script
MYSQLDUMPPATH=$MYSQLDUMPPATH
MYSQLPATH=$MYSQLPATH
LSDBHOST=$LSDBHOST
LSDB=$LSDB
LSUSER=$LSUSER
LSPASS=$LSPASS
CBDBHOST=$CBDBHOST
CBDB=$CBDB
CBUSER=$CBUSER
CBPASS=$CBPASS
GSDBHOST=$GSDBHOST
GSDB=$GSDB
GSUSER=$GSUSER
GSPASS=$GSPASS
EOF
chmod 600 $CONF
echo "Configuration saved as $CONF"
echo "Permissions changed to 600 (rw- --- ---)"
elif [ "$SAVE" != "n" -a "$SAVE" != "N" ]; then
  save_config
fi
}

load_config() {
if [ -n "$1" ]; then
CONF="$1"
else 
CONF="database_installer.rc"
fi
if [ -e "$CONF" ] && [ -f "$CONF" ]; then
. $CONF
else
echo "Settings file not found: $CONF"
echo "You can specify an alternate settings filename:"
echo $0 config_filename
echo ""
echo "If file doesn't exist it can be created"
echo "If nothing is specified script will try to work with ./database_installer.rc"
echo ""
configure $CONF
fi
}

#db_install(){
#  if [ -z "$1" ]
#    then
#      echo "No parameters."
#      exit $E_ARG_ERR
#  fi
#  
#  for file in $(ls $1/*sql);do
#    echo $file|cut -d/ -f4 >> database_installer.log
#    $MYG < $file 2>> database_installer.log
#	if [ $? -eq 0 ];then
#	  echo "no errors">> database_installer.log
#	fi   
#}

asklogin(){
echo "#############################################"
echo "# WARNING: This section of the script CAN   #"
echo "# destroy your characters and accounts      #"
echo "# information. Read questions carefully     #"
echo "# before you reply.                         #"
echo "#############################################"
echo ""
echo "Choose full (f) if you don't have and 'accounts' table or would"
echo "prefer to erase the existing accounts information."
echo "Choose skip (s) to skip loginserver DB installation and go to"
echo "communityserver DB installation/upgrade."
echo -ne "LOGINSERVER DB install type: (f) full, (s) skip or (q) quit? "
read LOGINPROMPT
case "$LOGINPROMPT" in
	"f"|"F") logininstall; loginupgrade; gsbackup; asktype;;
	"s"|"S") cbbackup; askcbtype;;
	"q"|"Q") finish;;
	*) asklogin;;
esac
}

logininstall(){
echo "Deleting loginserver tables for new content."
$MYL < login_install.sql &> /dev/null
}

loginupgrade(){
echo "Installling new loginserver content."
$MYL < ../sql/ls_sql/accounts.sql &> /dev/null
$MYL < ../sql/ls_sql/account_data.sql &> /dev/null
$MYL < ../sql/ls_sql/gameservers.sql &> /dev/null
}

gsbackup(){
while :
  do
   echo ""
   echo -ne "Do you want to make a backup copy of your GSDB? (y/n): "
   read LSB
   if [ "$LSB" == "Y" -o "$LSB" == "y" ]; then
     echo "Making a backup of the original gameserver database."
     $MYSQLDUMPPATH --add-drop-table -h $GSDBHOST -u $GSUSER --password=$GSPASS $GSDB > gameserver_backup.sql
     if [ $? -ne 0 ];then
     echo ""
     echo "There was a problem accesing your GS database, either it wasnt created or authentication data is incorrect."
     exit 1
     fi
     break
   elif [ "$LSB" == "n" -o "$LSB" == "N" ]; then 
     break
   fi
  done 
}

cbbackup(){
while :
  do
   echo ""
   echo -ne "Do you want to make a backup copy of your CBDB? (y/n): "
   read LSB
   if [ "$LSB" == "Y" -o "$LSB" == "y" ]; then
     echo "Making a backup of the original communityserver database."
     $MYSQLDUMPPATH --add-drop-table -h $CBDBHOST -u $CBUSER --password=$CBPASS $CBDB > communityserver_backup.sql
     if [ $? -ne 0 ];then
     echo ""
     echo "There was a problem accesing your CB database, either it wasnt created or authentication data is incorrect."
     exit 1
     fi
     break
   elif [ "$LSB" == "n" -o "$LSB" == "N" ]; then 
     break
   fi
  done 
}

lsbackup(){
while :
  do
   echo ""
   echo -ne "Do you want to make a backup copy of your LSDB? (y/n): "
   read LSB
   if [ "$LSB" == "Y" -o "$LSB" == "y" ]; then
     echo "Making a backup of the original loginserver database."
     $MYSQLDUMPPATH --add-drop-table -h $LSDBHOST -u $LSUSER --password=$LSPASS $LSDB > loginserver_backup.sql
     if [ $? -ne 0 ];then
        echo ""
        echo "There was a problem accesing your LS database, either it wasnt created or authentication data is incorrect."
        exit 1
     fi
     break
   elif [ "$LSB" == "n" -o "$LSB" == "N" ]; then 
     break
   fi
  done 
}

asktype(){
echo ""
echo ""
echo "WARNING: A full install (f) will destroy all existing character data."
echo -ne "GAMESERVER DB install type: (f) full install, (u) upgrade, (s) skip or (q) quit? "
read INSTALLTYPE
case "$INSTALLTYPE" in
	"f"|"F") fullinstall; upgradeinstall I; custom;;
	"u"|"U") upgradeinstall U; custom;;
	"s"|"S") custom;;
	"q"|"Q") finish;;
	*) asktype;;
esac
}

askcbtype(){
echo ""
echo ""
echo "WARNING: A full install (f) will destroy all existing community data."
echo -ne "COMMUNITYSERVER DB install type: (f) full install, (u) upgrade, (s) skip or (q) quit? "
read INSTALLTYPE
case "$INSTALLTYPE" in
	"f"|"F") fullcbinstall; upgradecbinstall I; gsbackup; asktype;;
	"u"|"U") upgradecbinstall U; gsbackup; asktype;;
	"s"|"S") gsbackup; asktype;;
	"q"|"Q") finish;;
	*) asktype;;
esac
}

fullcbinstall(){
echo "Deleting all communityserver tables for new content."
$MYG < community_install.sql &> /dev/null
}

upgradecbinstall(){
if [ "$1" == "I" ]; then 
echo "Installling new communityserver content."
else
echo "Upgrading communityserver content"
fi
if [ "$1" == "I" ]; then
$MYG < ../sql/cb_sql/clan_introductions.sql &> /dev/null
$MYG < ../sql/cb_sql/comments.sql &> /dev/null
$MYG < ../sql/cb_sql/forums.sql &> /dev/null
$MYG < ../sql/cb_sql/registered_gameservers.sql &> /dev/null
$MYG < ../sql/cb_sql/posts.sql &> /dev/null
$MYG < ../sql/cb_sql/topics.sql &> /dev/null
fi
newbie_helper_cb
}

fullinstall(){
echo "Deleting all gameserver tables for new content."
$MYG < full_install.sql &> /dev/null
}

upgradeinstall(){
if [ "$1" == "I" ]; then 
echo "Installling new gameserver content."
else
echo "Upgrading gameserver content"
fi
$MYG < ../sql/gs_sql/access_levels.sql &> /dev/null
$MYG < ../sql/gs_sql/account_premium.sql &> /dev/null
$MYG < ../sql/gs_sql/admin_command_access_rights.sql &> /dev/null
$MYG < ../sql/gs_sql/airships.sql &> /dev/null
$MYG < ../sql/gs_sql/armorsets.sql &> /dev/null
$MYG < ../sql/gs_sql/auction.sql &> /dev/null
$MYG < ../sql/gs_sql/auction_bid.sql &> /dev/null
$MYG < ../sql/gs_sql/auction_watch.sql &> /dev/null
$MYG < ../sql/gs_sql/auto_announcements.sql &> /dev/null
$MYG < ../sql/gs_sql/auto_chat_text.sql &> /dev/null
$MYG < ../sql/gs_sql/auto_chat.sql &> /dev/null
$MYG < ../sql/gs_sql/castle.sql &> /dev/null
$MYG < ../sql/gs_sql/castle_door.sql &> /dev/null
$MYG < ../sql/gs_sql/castle_doorupgrade.sql &> /dev/null
$MYG < ../sql/gs_sql/castle_functions.sql &> /dev/null
$MYG < ../sql/gs_sql/castle_manor_procure.sql &> /dev/null
$MYG < ../sql/gs_sql/castle_manor_production.sql &> /dev/null
$MYG < ../sql/gs_sql/castle_siege_guards.sql &> /dev/null
$MYG < ../sql/gs_sql/char_templates.sql &> /dev/null
$MYG < ../sql/gs_sql/char_creation_items.sql &> /dev/null
$MYG < ../sql/gs_sql/character_friends.sql &> /dev/null
$MYG < ../sql/gs_sql/character_hennas.sql &> /dev/null
$MYG < ../sql/gs_sql/character_instance_time.sql &> /dev/null
$MYG < ../sql/gs_sql/character_macroses.sql &> /dev/null
$MYG < ../sql/gs_sql/character_premium_items.sql &> /dev/null
$MYG < ../sql/gs_sql/character_quest_global_data.sql &> /dev/null 
$MYG < ../sql/gs_sql/character_offline_trade_items.sql &> /dev/null
$MYG < ../sql/gs_sql/character_offline_trade.sql &> /dev/null
$MYG < ../sql/gs_sql/character_quests.sql &> /dev/null
$MYG < ../sql/gs_sql/character_raid_points.sql &> /dev/null
$MYG < ../sql/gs_sql/character_recipebook.sql &> /dev/null
$MYG < ../sql/gs_sql/character_recipeshoplist.sql &> /dev/null
$MYG < ../sql/gs_sql/character_reco_bonus.sql &> /dev/null
$MYG < ../sql/gs_sql/character_shortcuts.sql &> /dev/null
$MYG < ../sql/gs_sql/character_skills.sql &> /dev/null
$MYG < ../sql/gs_sql/character_skills_save.sql &> /dev/null
$MYG < ../sql/gs_sql/character_subclasses.sql &> /dev/null
$MYG < ../sql/gs_sql/character_tpbookmark.sql &> /dev/null
$MYG < ../sql/gs_sql/character_ui_actions.sql &> /dev/null
$MYG < ../sql/gs_sql/character_ui_categories.sql &> /dev/null
$MYG < ../sql/gs_sql/character_summons.sql &> /dev/null
$MYG < ../sql/gs_sql/character_summon_skills_save.sql &> /dev/null
$MYG < ../sql/gs_sql/character_pet_skills_save.sql &> /dev/null
$MYG < ../sql/gs_sql/characters.sql &> /dev/null
$MYG < ../sql/gs_sql/clan_data.sql &> /dev/null
$MYG < ../sql/gs_sql/clan_notices.sql &> /dev/null
$MYG < ../sql/gs_sql/clan_privs.sql &> /dev/null
$MYG < ../sql/gs_sql/clan_skills.sql &> /dev/null
$MYG < ../sql/gs_sql/clan_subpledges.sql &> /dev/null
$MYG < ../sql/gs_sql/clan_wars.sql &> /dev/null
$MYG < ../sql/gs_sql/clanhall.sql &> /dev/null
$MYG < ../sql/gs_sql/clanhall_siege.sql &> /dev/null
$MYG < ../sql/gs_sql/clanhall_siege_guards.sql &> /dev/null
$MYG < ../sql/gs_sql/clanhall_functions.sql &> /dev/null
$MYG < ../sql/gs_sql/clanhall_siege.sql &> /dev/null
$MYG < ../sql/gs_sql/castle_siege_guards.sql &> /dev/null
$MYG < ../sql/gs_sql/class_list.sql &> /dev/null
$MYG < ../sql/gs_sql/cursed_weapons.sql &> /dev/null
$MYG < ../sql/gs_sql/dimensional_rift.sql &> /dev/null
$MYG < ../sql/gs_sql/droplist.sql &> /dev/null
$MYG < ../sql/gs_sql/enchant_skill_groups.sql &> /dev/null
$MYG < ../sql/gs_sql/fish.sql &> /dev/null
$MYG < ../sql/gs_sql/fishing_skill_trees.sql &> /dev/null
$MYG < ../sql/gs_sql/fort.sql &> /dev/null
$MYG < ../sql/gs_sql/fort_doorupgrade.sql &> /dev/null
$MYG < ../sql/gs_sql/fort_functions.sql &> /dev/null
$MYG < ../sql/gs_sql/fort_siege_guards.sql &> /dev/null
$MYG < ../sql/gs_sql/fort_spawnlist.sql &> /dev/null
$MYG < ../sql/gs_sql/fort_staticobjects.sql &> /dev/null
$MYG < ../sql/gs_sql/fortsiege_clans.sql &> /dev/null
$MYG < ../sql/gs_sql/forums.sql &> /dev/null
$MYG < ../sql/gs_sql/four_sepulchers_spawnlist.sql &> /dev/null
$MYG < ../sql/gs_sql/games.sql &> /dev/null
$MYG < ../sql/gs_sql/global_tasks.sql &> /dev/null
$MYG < ../sql/gs_sql/global_variables.sql &> /dev/null
$MYG < ../sql/gs_sql/grandboss_data.sql &> /dev/null
$MYG < ../sql/gs_sql/grandboss_list.sql &> /dev/null
$MYG < ../sql/gs_sql/helper_buff_list.sql &> /dev/null
$MYG < ../sql/gs_sql/henna.sql &> /dev/null
$MYG < ../sql/gs_sql/henna_trees.sql &> /dev/null
$MYG < ../sql/gs_sql/herb_droplist_groups.sql &> /dev/null
$MYG < ../sql/gs_sql/heroes.sql &> /dev/null
$MYG < ../sql/gs_sql/heroes_diary.sql &> /dev/null
$MYG < ../sql/gs_sql/item_attributes.sql &> /dev/null
$MYG < ../sql/gs_sql/item_auction_bid.sql &> /dev/null
$MYG < ../sql/gs_sql/item_auction.sql &> /dev/null
$MYG < ../sql/gs_sql/item_elementals.sql &> /dev/null
$MYG < ../sql/gs_sql/items.sql &> /dev/null
$MYG < ../sql/gs_sql/itemsonground.sql &> /dev/null
$MYG < ../sql/gs_sql/locations.sql &> /dev/null
$MYG < ../sql/gs_sql/lvlupgain.sql &> /dev/null
$MYG < ../sql/gs_sql/mapregion.sql &> /dev/null
$MYG < ../sql/gs_sql/merchant_buylists.sql &> /dev/null
$MYG < ../sql/gs_sql/merchant_lease.sql &> /dev/null
$MYG < ../sql/gs_sql/merchant_shopids.sql &> /dev/null
$MYG < ../sql/gs_sql/messages.sql &> /dev/null
$MYG < ../sql/gs_sql/minions.sql &> /dev/null
$MYG < ../sql/gs_sql/npc.sql &> /dev/null
$MYG < ../sql/gs_sql/npc_buffer.sql &> /dev/null
$MYG < ../sql/gs_sql/npcaidata.sql &> /dev/null
$MYG < ../sql/gs_sql/npc_elementals.sql &> /dev/null
$MYG < ../sql/gs_sql/npcskills.sql &> /dev/null
$MYG < ../sql/gs_sql/olympiad_data.sql &> /dev/null
$MYG < ../sql/gs_sql/olympiad_fights.sql &> /dev/null
$MYG < ../sql/gs_sql/olympiad_nobles.sql&> /dev/null
$MYG < ../sql/gs_sql/olympiad_nobles_eom.sql&> /dev/null
$MYG < ../sql/gs_sql/pets.sql &> /dev/null
$MYG < ../sql/gs_sql/pets_skills.sql &> /dev/null
$MYG < ../sql/gs_sql/pledge_skill_trees.sql &> /dev/null
$MYG < ../sql/gs_sql/posts.sql &> /dev/null
$MYG < ../sql/gs_sql/quest_global_data.sql &> /dev/null
$MYG < ../sql/gs_sql/raidboss_spawnlist.sql &> /dev/null
$MYG < ../sql/gs_sql/random_spawn.sql &> /dev/null
$MYG < ../sql/gs_sql/random_spawn_loc.sql &> /dev/null
$MYG < ../sql/gs_sql/seven_signs.sql &> /dev/null
$MYG < ../sql/gs_sql/seven_signs_festival.sql &> /dev/null
$MYG < ../sql/gs_sql/seven_signs_status.sql &> /dev/null
$MYG < ../sql/gs_sql/siege_clans.sql &> /dev/null
$MYG < ../sql/gs_sql/skill_learn.sql &> /dev/null
$MYG < ../sql/gs_sql/skill_spellbooks.sql &> /dev/null
$MYG < ../sql/gs_sql/skill_trees.sql &> /dev/null
$MYG < ../sql/gs_sql/skill_residential.sql &> /dev/null
$MYG < ../sql/gs_sql/spawnlist.sql &> /dev/null
$MYG < ../sql/gs_sql/special_skill_trees.sql &> /dev/null
$MYG < ../sql/gs_sql/teleport.sql &> /dev/null
$MYG < ../sql/gs_sql/topic.sql &> /dev/null
$MYG < ../sql/gs_sql/territories.sql &> /dev/null
$MYG < ../sql/gs_sql/territory_registrations.sql &> /dev/null
$MYG < ../sql/gs_sql/territory_spawnlist.sql &> /dev/null
$MYG < ../sql/gs_sql/transform_skill_trees.sql &> /dev/null
$MYG < ../sql/gs_sql/DM.sql &> /dev/null


$MYG < ../sql/l2universe/needed/Procedures.sql &> /dev/null

$MYG < ../sql/l2universe/added/clanhall_siege_attackers.sql &> /dev/null
$MYG < ../sql/l2universe/added/droplist.sql &> /dev/null
$MYG < ../sql/l2universe/added/L2Universe.sql &> /dev/null
$MYG < ../sql/l2universe/added/npcCharData.sql &> /dev/null
$MYG < ../sql/l2universe/added/npcskills.sql &> /dev/null 
$MYG < ../sql/l2universe/added/petition_feedback.sql &> /dev/null
$MYG < ../sql/l2universe/added/rainbowsprings_attacker_list.sql &> /dev/null
$MYG < ../sql/l2universe/added/siegable_clanhall.sql &> /dev/null
$MYG < ../sql/l2universe/added/spawnlist.sql &> /dev/null
$MYG < ../sql/l2universe/added/teleport.sql &> /dev/null
$MYG < ../sql/l2universe/added/donate_items.sql &> /dev/null
$MYG < ../sql/l2universe/added/comteleport.sql &> /dev/null
$MYG < ../sql/l2universe/added/ctf.sql &> /dev/null
$MYG < ../sql/l2universe/added/ctf_teams.sql &> /dev/null
$MYG < ../sql/l2universe/added/chatdata.sql &> /dev/null
$MYG < ../sql/l2universe/added/rainbowsprings_attacker_list.sql &> /dev/null
$MYG < ../sql/l2universe/added/record.sql &> /dev/null 
$MYG < ../sql/l2universe/added/hellbound_data.sql &> /dev/null
$MYG < ../sql/l2universe/added/hellbound_spawnlist.sql &> /dev/null
$MYG < ../sql/l2universe/added/HideAndSeek.sql &> /dev/null
$MYG < ../sql/l2universe/added/irc.sql &> /dev/null
$MYG < ../sql/l2universe/added/kamaloka_results.sql &> /dev/null
$MYG < ../sql/l2universe/added/krateis_cube.sql &> /dev/null
$MYG < ../sql/l2universe/added/bot_report.sql &> /dev/null
$MYG < ../sql/l2universe/added/bot_reported_punish.sql &> /dev/null
$MYG < ../sql/l2universe/added/buffer_buff_list.sql &> /dev/null
$MYG < ../sql/l2universe/added/buffer_config_sections.sql &> /dev/null
$MYG < ../sql/l2universe/added/buffer_configuration.sql &> /dev/null
$MYG < ../sql/l2universe/added/buffer_scheme_contents.sql &> /dev/null
$MYG < ../sql/l2universe/added/buffer_scheme_list.sql &> /dev/null

$MYG < ../sql/l2universe/added/aio_buffs.sql &> /dev/null
$MYG < ../sql/l2universe/added/aio_scheme_buffs.sql &> /dev/null
$MYG < ../sql/l2universe/added/aio_scheme_profiles_buffs.sql &> /dev/null
$MYG < ../sql/l2universe/added/aio_scheme_profiles.sql &> /dev/null
$MYG < ../sql/l2universe/added/aio_teleports_categories.sql &> /dev/null
$MYG < ../sql/l2universe/added/Oren_school.sql &> /dev/null
$MYG < ../sql/l2universe/added/aio_teleports.sql &> /dev/null
}

custom(){
echo ""
echo ""
echo -ne "Install custom gameserver DB tables: (y) yes or (n) no or (q) quit?"
read ASKCS
case "$ASKCS" in
	"y"|"Y") cstinstall;;
	"n"|"N") finish;;
	"q"|"Q") finish;;
	*) custom;;
esac
finish
}

cstinstall(){
while :
  do
   echo ""
   echo -ne "Do you want to make another backup of GSDB before applying custom contents? (y/N): "
   read LSB
   if [ "$LSB" == "Y" -o "$LSB" == "y" ]; then
     echo "Making a backup of the default gameserver tables."
     $MYSQLDUMPPATH --add-drop-table -h $GSDBHOST -u $GSUSER --password=$GSPASS $GSDB > custom_backup.sql 2> /dev/null
     if [ $? -ne 0 ];then
     echo ""
     echo "There was a problem accesing your GS database, server down?."
     exit 1
     fi
     break
   elif [ "$LSB" == "n" -o "$LSB" == "N" -o "$LSB" == "" ]; then 
     break
   fi
  done 
echo "Installing custom content."
for custom in $(ls ../sql/gs_sql/custom/*.sql);do 
$MYG < $custom &> /dev/null
done
# l2universe mods that needed extra tables to work properly, should be 
# listed here. To do so copy & paste the following 6 lines and
# change them properly:
# MOD: Wedding.
  echo -ne "Install "Wedding Mod" tables? (y/N): "
  read modprompt
  if [ "$modprompt" == "Y" -o "$modprompt" == "y" ]; then
		$MYG < ../sql/gs_sql/mods/mods_wedding.sql &> /dev/null
	fi

finish
}

finish(){
echo ""
echo "Script execution finished."
exit 0
}

newbie_helper(){
while :
  do
   echo ""
   echo -ne "If you're not that skilled applying changes within 'updates' folder, i can try to do it for you (y). If you wish to do it on your own, choose (n). Should i parse updates files? (Y/n)"
   read NOB
   if [ "$NOB" == "Y" -o "$NOB" == "y" -o "$NOB" == "" ]; then
     echo ""
     echo "There we go, it may take some time..."
     echo "updates parser results. Last run: "`date` >database_installer.log
     for file in $(ls ../sql/updates/*sql);do
        echo $file|cut -d/ -f4 >> database_installer.log
        $MYG < $file 2>> database_installer.log
	if [ $? -eq 0 ];then
	    echo "no errors">> database_installer.log
	fi    
	done
     echo ""
     echo "Log available at $(pwd)/database_installer.log"
     echo ""
     break
   elif [ "$NOB" == "n" -o "$NOB" == "N" ]; then 
     break
   fi
  done 
}

newbie_helper_cb(){
while :
  do
   echo ""
   echo -ne "If you're not that skilled applying changes within 'updates' folder, i can try to do it for you (y). If you wish to do it on your own, choose (n). Should i parse updates files? (Y/n)"
   read NOB
   if [ "$NOB" == "Y" -o "$NOB" == "y" -o "$NOB" == "" ]; then
     echo ""
     echo "There we go, it may take some time..."
     echo "updates parser results. Last run: "`date` >cb_database_installer.log
     for file in $(ls ../cb_sql/updates/*sql);do
        echo $file|cut -d/ -f4 >> cb_database_installer.log
        $MYG < $file 2>> cb_database_installer.log
        if [ $? -eq 0 ];then
            echo "no errors">> cb_database_installer.log
        fi
     done
     echo ""
     echo "Log available at $(pwd)/cb_database_installer.log"
     echo ""
     break
   elif [ "$NOB" == "n" -o "$NOB" == "N" ]; then
     break
   fi
  done 
}

clear
load_config $1
MYL="$MYSQLPATH -h $LSDBHOST -u $LSUSER --password=$LSPASS -D $LSDB"
MYG="$MYSQLPATH -h $GSDBHOST -u $GSUSER --password=$GSPASS -D $GSDB"
MYC="$MYSQLPATH -h $CBDBHOST -u $CBUSER --password=$CBPASS -D $CBDB"
lsbackup
asklogin