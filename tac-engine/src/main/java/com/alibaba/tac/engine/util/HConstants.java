/**
 * Copyright 2010 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.tac.engine.util;

/**
 * HConstants holds a bunch of HBase-related constants
 */
public final class HConstants {


  /**
   * An empty instance.
   */
  public static final byte [] EMPTY_BYTE_ARRAY = new byte [0];


  /** When we encode strings, we always specify UTF8 encoding */
  public static final String UTF8_ENCODING = "UTF-8";

  private HConstants() {
    // Can't be instantiated with this ctor.
  }
}
