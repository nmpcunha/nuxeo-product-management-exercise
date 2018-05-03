/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Contributors:
 *      nuno
 */

package org.nuxeo.onboarding.exercise.constants.model;

import java.util.Arrays;

import org.nuxeo.onboarding.exercise.constants.ProductSchemas;

public enum NxProduct {

    AVAILABLE_IMMEDIATELY("available_immediately"),
    CATEGORY("category"),
    DELIVERY_TIME("delivery_time"),
    DESCRIPTION("description"),
    DISTRIBUTOR("distributor"),
    DISTRIBUTOR_NAME("name"),
    DISTRIBUTOR_SELL_LOCATION("sell_location"),
    NAME("name"),
    ORIGIN_OF_FABRICATION("origin_of_fabrication"),
    PRICE("price"),
    PRODUCT_ID("product_id"),
    SIZE("size");

    private String propertyKey;

    NxProduct(String propertyKey){
        this.propertyKey = propertyKey;
    }

    public String getPropertyKey(){
        return propertyKey;
    }

    public String getPropertyXPath() {
        return String.join(":", Arrays.asList(ProductSchemas.PRODUCT.getName(), propertyKey));
    }

}
