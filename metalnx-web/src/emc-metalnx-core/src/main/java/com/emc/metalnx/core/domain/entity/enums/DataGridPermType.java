/*
 *    Copyright (c) 2015-2016, EMC Corporation
 *
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
package com.emc.metalnx.core.domain.entity.enums;

/*
 * Defines all permission types that exist in the data grid.
 * */

public enum DataGridPermType {

    OWN("OWN"), WRITE("WRITE"), READ("READ"), IRODS_ADMIN("ADMIN"), NONE("NONE");

    private String stringType;

    DataGridPermType(String type) {
        this.stringType = type.toUpperCase();
    }

    @Override
    public String toString() {
        return this.stringType;
    }

}