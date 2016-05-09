#The Open Script Repository
The Open Script Repository allows non-ranked script writers to publish their scripts to SDN.
<p>
If you have a script that you would like to publish, read on.

###Setting Up
1. Fork this repository
2. Add the remote
    - `git remote add origin https://github.com/[your github name]/powerbot.git`
3. Add the upstream source
    - `git remote add upstream https://github.com/powerbot/powerbot.git`
4. Fetch the repository
    - `git fetch origin`
5. Change your working branch to the 'scripts' branch
    - `git checkout scripts`
    
###Adding your Scripts
1. Sync your local copy with the upstream source
    - `git fetch upstream`
2. Rebase
    - `git rebase upstream/scripts`
3. Commit your changes
    - `git commit -a -m "Your update message"`
4. Push the changes to your fork
    - `git push origin scripts`
5. Make a Pull Request from your forked repository

###Guidelines for Scripts
- Scripts **must** be packaged
- The parent package **must** be your powerbot username (e.g. coma/scripts/Script.java)
- Scripts **must** have an `author` tag in the script `Properties` with your powerbot username
- Scripts **must** have a `topic` tag with a thread ID corresponding to your script thread in the [Projects](http://www.powerbot.org/community/forum/55-projects/) section
- Failure to meet any of these guidelines will result in a denied pull request