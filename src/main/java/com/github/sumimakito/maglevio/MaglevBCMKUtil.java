/*
 * Copyright 2014-2015 Sumi Makito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sumimakito.maglevio;

public class MaglevBCMKUtil {
    public static class Stopwatch{
        private long srcTS = 0;
        private long lastTS = 0;
        private int cpId = 0;
        public Stopwatch(){
            this.reset();
        }
        public void reset(){
            this.srcTS = System.currentTimeMillis();
            this.lastTS = this.srcTS;
        }
        public void checkPoint(){
            checkPoint("");
        }
        public void checkPoint(String msg){
            long cTS = System.currentTimeMillis();
            System.out.println("Checkpoint: ["+(++cpId)+"] M:["+msg+"] C:["+(cTS-this.lastTS)+"ms] T:["+(cTS-this.srcTS)+"]");
            lastTS = System.currentTimeMillis();
        }
    }
}
