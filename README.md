My senior design project from Spring 2017. Sponsored by DellEMC

## Notes about program:

#### Accept/reject results will be in serverLog
Logs in: `/var/lib/irods/iRODS/server/log/rodsLog<date>`

Monitor logs live with `sudo tail -f <filename>`

#### Remote Files
Files on remote server will be in `testfiles` dir.

View files in iRODS space: `ils testfiles`

Pull files into local file system: `iget testfiles/<filename>`

Remove file from iRODS space: `irm testfiles/<filename>`

Script to remove all files: `/home/dlgross/removeFiles.sh`

#### JUnit
Currently just tests that all files can be sent to remote server.

Does not check accept/reject status due to lack of status being returned to Java. Will address soon.

#### rule.txt
This is the rule from core.re that is hooked into receiving the text files in /tempZone/home/rods/testfiles/.

Currently it is named acPostProcForPut. It may be okay to overload this rule, but it also may be better to figure out how to hook another name in. This name can be changed by uncommenting `testFileWatc {`.
