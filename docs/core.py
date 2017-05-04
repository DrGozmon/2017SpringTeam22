import errno
import json
import os

# accepts rule file name and text of rule, deletes rule file from /etc/irods and removes rule_engine name from server_config.json data
def deleteRuleFile(rule_args, callback):
    # get filename from arguments
    filename = getFilename(rule_args[0])
    filename_prefix = filename.split(".")[0] # strip file extension

    ### delete file from "/etc/irods/"
    deleteFileByName("/etc/irods/" + filename, callback)

    ### remove reference to file from server_config.json
    json_data = getJSONDataFromFile("/etc/irods/server_config.json") # read json_data from server_config.json
    re_val = findRuleEngineIndex(json_data, "irods_rule_engine_plugin-irods_rule_language")
    if re_val == -1: # return value that indicates the rule_engine was not found
        callback.writeLine('serverLog', 'Could not find plugin in server_config.json; did not remove.')
        callback.msiExit("-1","Plugin not found")

    rule_engine = json_data["plugin_configuration"]["rule_engines"][re_val] # reference to rule_engine in json_data

    # search through rule_engine's rulebase_set to find given filename
    if filename_prefix not in rule_engine["plugin_specific_configuration"]["re_rulebase_set"]:
        callback.writeLine('serverLog', 'Could not find filename in server_config.json; did not remove.')
        callback.msiExit("-1","Filename not found in rule_engine")

    # remove filename from rule_engine's rulebase_set
    rule_engine["plugin_specific_configuration"]["re_rulebase_set"].remove(filename_prefix)

    # write json_data back to server_config.json
    writeJSONDataToFile(json_data,"/etc/irods/server_config.json")

# deletes given filename from system.
# Returns if successful; calls msiExit if not
def deleteFileByName(filename, callback):
    try:
        os.remove(filename)
    except OSError as e:
        if e.errno == errno.ENOENT:
            # OSError that is file not found; will still attempt to remove from JSON data
            callback.writeLine('serverLog', 'File not found; could not delete file.')
        else:
            # OSError that is not file not found; will abort
            callback.writeLine('serverLog', 'Fatal error while trying to remove ' + filename)
            callback.msiExit("-100","Unknown, fatal error.")

# finds the correct rule engine (named by re_name) in the given json_data
# returns the index of the rule_engine
def findRuleEngineIndex(json_data, re_name):
    rule_engines = json_data["plugin_configuration"]["rule_engines"] # locate rule_engines in json_data

    # search through all rule_engines to find the one that matches the give name
    l = len(rule_engines)
    for i in range(l):
        if rule_engines[i]["plugin_name"] == re_name:
            break

    if rule_engines[i]["plugin_name"] != re_name:
        return -1 # could not find irods_rule_language plugin; fail
    return i # return index of rule_engine

# accepts rule file name and text of rule; creates rule file and adds to server_config.json data
def deployRuleFile(rule_args, callback):
    # parse arguments
    filename = getFilename(rule_args[0])
    filename_prefix = filename.split(".")[0] # strip file extension
    text,overwrite = parseText(rule_args[1].split("\n")) # strips first line from text (e.g. deploy, delete)

    ### add file to /etc/irods/
    # create new filepath; check if file already exists
    outpath = "/etc/irods/" + filename
    if os.path.exists(outpath):
        if not overwrite:
            callback.writeLine('serverLog', 'File is already present in /etc/irods/; overwrite not selected')
            callback.msiExit("-1", "File already exists")
        else:
            callback.writeLine('serverLog', 'File is already present in /etc/irods/; overwrite selected')
            deleteFileByName(outpath,callback)

    # open new file in /etc/irods/ to write contents to as new *.re file
    f = open(outpath,"w")
    for line in text:
        f.write(line + "\n")
    f.close()

    ### add new file to server_config.json rulebase set
    updateServerConfigJSON(filename_prefix,callback)

# update the server_config.json file
def updateServerConfigJSON(new_re_name,callback):
    # get json_data from server_config.json, find rule engine in dict
    json_data = getJSONDataFromFile("/etc/irods/server_config.json")
    re_val = findRuleEngineIndex(json_data, "irods_rule_engine_plugin-irods_rule_language")
    if re_val == -1:
        callback.writeLine('serverLog', 'Could not find plugin in server_config.json; did not add.')
        callback.msiExit("-2","server_config.json write failed")

    rule_engines = json_data["plugin_configuration"]["rule_engines"] # reference to rule_engines in json_data

    # check that the new_re_name is not already present in the rule_engine
    if new_re_name in rule_engines[re_val]["plugin_specific_configuration"]["re_rulebase_set"]:
        callback.writeLine('serverLog', 'Rule file already present in server_config.json; did not add again.')
        return

    # add new_re_name to rule_engine in server_config.json
    rule_engines[re_val]["plugin_specific_configuration"]["re_rulebase_set"].insert(0, new_re_name)

    # write new data to server_config.json
    writeJSONDataToFile(json_data,"/etc/irods/server_config.json")

# read json_data from the given file
# returns json_data dict
def getJSONDataFromFile(filename):
    sc_file = open(filename,"r")
    json_data = json.load(sc_file)
    sc_file.close()

    return json_data

# write json_data dict to given file
def writeJSONDataToFile(json_data, filename):
    sc_file = open(filename,"w")
    json.dump(json_data,sc_file,indent=4,sort_keys=True)
    sc_file.write("\n")
    sc_file.close()

# assume a form of /path/to/rulefile/timestamp-desiredfilename.re for teh given filepath
#returns only desiredfilename.re portion
def getFilename(filepath):
    return filepath.split("-")[-1]

# cleans up the passed in text string by stripping off "deploy" at the beginning and trimming blank lines from the end of the file
# returns string of cleaned up text
def parseText(text):
    # get overwrite value from first line
    firstLine = text[0] # keep first line; will include "deploy" and overwrite value
    overwrite = False
    if firstLine.split(",")[1] == "overwrite=yes":
        overwrite = True

    text = text[1:] # remove first line

    # read overwrite

    # strip off excess blank lines at the end of the file
    while text[-1] == "":
        text = text[:-1]

    return text, overwrite
