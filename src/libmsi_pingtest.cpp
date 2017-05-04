#include "rods.h"
#include "reGlobalsExtern.hpp"
#include "irods_ms_plugin.hpp"
#include "modAVUMetadata.h"
#include "reFuncDefs.hpp"
#include "apiHeaderAll.h"
// #include "objMetaOpr.hpp"
// #include "dataObjOpr.hpp"
#include "physPath.hpp"
#include "miscServerFunct.hpp"
#include "rcGlobalExtern.h"
#include "reGlobalsExtern.hpp"
#include "irods_log.hpp"
#include "irods_file_object.hpp"
#include "irods_stacktrace.hpp"
#include "irods_resource_redirect.hpp"

#include <iostream>
#include <string>
#include <fstream>
using namespace std;

extern "C" {

    /*
     * A function that writes to a file
     */
    int write_to_file( const char* input ) {
        const char* filename = "rulefile.txt";

        // Append new rule(s) to bottom of ruleset file.
        ofstream ruleset_file ( filename, ios::app );
        ruleset_file << endl << input << endl;

        return 0;
    }

    /*
     * A sort of "main" function
     */
    int msi_pingtest() {
        cout << write_to_file("new rule");

        return 0;
    }

    // =-=-=-=-=-=-=-
    // plugin factory
    irods::ms_table_entry*  plugin_factory( ) {
        // =-=-=-=-=-=-=-
        // instantiate a new msvc plugin
        irods::ms_table_entry* msvc = new irods::ms_table_entry( 1 );

        // =-=-=-=-=-=-=-
        // wire the implementation to the plugin instance
        msvc->add_operation( "msi_pingtest", "msi_pingtest" );

        // =-=-=-=-=-=-=-
        // hand it over to the system
        return msvc;

    } // plugin_factory

} // extern "C"
