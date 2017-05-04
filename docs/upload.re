acPostProcForPut {
  ON($objPath like "/tempZone/home/rods/.rulecache/*.re") {
    handleNewRuleFileUpload;
  }
}

handleNewRuleFileUpload {
#  writeLine("serverLog", "Input file: $objPath"); # debug print

  ### read text from input file
  msiDataObjOpen($objPath,*file);
  msiDataObjRead(*file,10000,*text);
  msiDataObjClose(*file,*Status);
#  writeLine("serverLog", "*text"); # debug print

  ### open results file for writeback
  *outfile=$objPath++".res"; # filepath for results file
  msiDataObjCreate(*outfile,"forceFlag=",*out_buf);
#  writeLine("serverLog", "Output file: *outfile");

  ### determine response, write to file
  if ("*text" like "deploy*") {
    writeLine("serverLog", "DEPLOYING this rule file.");
    *result=errorcode(deployRuleFile($objPath,"*text")); # send text to Python to write file on system
    if (*result == 0) {
      writeLine("serverLog","Deployed");
      msiDataObjWrite(*out_buf,"Deployed",*Status);
    } else {
      if (*result == -1) {
        # file already exists; overwrite not selected
        msiDataObjWrite(*out_buf,"File already present; did not deploy",*Status);
      } else {
        if (*result == -2) {
          # server_config.json write failed
          msiDataObjWrite(*out_buf,"Write failed to server_config.json",*Status);
        } else {
          # unknown error occurred
          msiDataObjWrite(*out_buf,"Unknown error occurred during deploy",*Status);
        }
      }
    }
  } else {
    if ("*text" like "delete*") {
      writeLine("serverLog", "DELETING this rule file.");
      *result=errorcode(deleteRuleFile($objPath,"*text")); # send filename to Python to remove file on system
      if (*result == 0) {
        writeLine("serverLog","Deleted");
        msiDataObjWrite(*out_buf,"Deleted",*Status);
      } else {
        if (*result == -1) {
          msiDataObjWrite(*out_buf,"File not found or not present in server_config.json",*Status);
        } else {
          # unknown error occurred
          msiDataObjWrite(*out_buf,"Unknown error occurred during delete",*Status);
        }
      }
    } else {
      writeLine("serverLog", "No message found. IGNORE this rule file.");
      msiDataObjWrite(*out_buf,"No action taken",*Status);
    }
  }
  msiDataObjClose(*out_buf,*Status);
}
