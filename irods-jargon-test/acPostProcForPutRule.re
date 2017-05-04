acPostProcForPut {
  ON($objPath like "/tempZone/home/rods/.rulecache/*.re") {
    writeLine("serverLog", "*.re file detected!");
    handleNewRuleFileUpload;
  }
}