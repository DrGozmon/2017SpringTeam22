/* @filename   ping.c
   @author     mphayes2@ncsu.edu 
   @created    13 Feb 2017
   @modified   13 Feb 2017
  
  This function is a microservice to be used in iRODS. An
  external service (remote pc or Metalnx server) will pass
  a char containing one or more rules to be applied to the
  grid. This function willthen  append those new rules to
  the bottom of the designated ruleset file on each node 
  in this grid. */

#include <iostream>
#include <string>
#include <fstream>
using namespace std;

const char* msi_ping( const char* input ) {
  const char* filename = "rulefile.txt";

  // Append new rule(s) to bottom of ruleset file.
  ofstream ruleset_file ( filename, ios::app );
  ruleset_file << endl << input << endl;

  // TODO: assert that rules were added, return success.
  return "done";
}

int main() { cout << msi_ping("new rule"); }