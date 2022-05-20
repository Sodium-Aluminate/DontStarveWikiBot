# WikiBot
some tools for dst-zh wiki.

## How To Use?
### get bot password
go to Special:BotPasswords in your wiki, generate a bot. note: you should mark this account as robot, or the "recent changes" page will be spammed.

set env: `WIKI_USERNAME` and `WIKI_PASSWD`, you can use `export` command or just `WIKI_USERNAME=NaAlOH4 WIKI_PASSWD=NaAlOH4@114514 java -jar 1919810.jar`

Not unix system? you must edit all the source code related file, and get something can replace bash in Windows...

### Update (or generate) Template "SmallIconAndText"
(need login) just keep it running by systemd or anything else you like.

### Update (or generate) Po template
0. make sure you have installed sed, unzip, bash; and you can access /tmp dir
1. find `scripts.zip` from your game install dir, and edit `scripts_dist` in `preparePo` file in this repo. 
2. run preparePo.
3. copy Don't Starve chinese_s.po to /tmp/dstScripts/chinese_s.po
4. run PoParser.PoSorter in src, it will split all po file into lua data.
5. (need admin bot login) run UpdatePo, it will upload `Module:Map` and `Module:MapChunk_n` to wiki
6. (first run) copy https://dontstarve.fandom.com/zh/wiki/Module:FromCode to your wiki.

#### not chinese wiki?
First, sorry for my design, I didn't consider using this module for other languages, but I believed it's easy to change the source code to fit your language.

If your target language is not Chinese, you must replace all "chinese_s" to your target language.

And the url "https://dontstarve.fandom.com/zh/api.php" need to change to correct url.

when you copy Module:FromCode, you should note that: "zh" means chinese, I suggest to change to your target language code.

**If your target is English, you need gen another po file that "translate" from English to English.**