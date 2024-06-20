/*
 * Copyright 2022 jrosclient project
 * 
 * Website: https://github.com/lambdaprime/jros2client
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.jros2client.qos;

/**
 * @see <a
 *     href="https://docs.ros.org/en/rolling/Concepts/Intermediate/About-Quality-of-Service-Settings.html">Quality
 *     of Service settings</a>
 * @author lambdaprime intid@protonmail.com
 */
public enum QosDurability {
    /** No attempt is made to persist samples. */
    VOLATILE,

    /**
     * The publisher becomes responsible for persisting samples for "late-joining" subscriptions.
     */
    TRANSIENT_LOCAL,
}
