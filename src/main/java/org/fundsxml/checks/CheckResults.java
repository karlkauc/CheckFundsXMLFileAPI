/*
 * Copyright (c) 2022 Karl Kauc.
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
 *
 *
 */

package org.fundsxml.checks;

public class CheckResults {

    RESULTS resultStatus;
    int checkNumber;
    String checkName;
    String errorMessage;
    String errorDetails;

    public RESULTS getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(RESULTS resultStatus) {
        this.resultStatus = resultStatus;
    }

    public int getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(int checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    @Override
    public String toString() {
        return "CheckResults{" +
                "resultStatus=" + resultStatus +
                ", checkNumber=" + checkNumber +
                ", checkName='" + checkName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorDetails='" + errorDetails + '\'' +
                '}';
    }

    public enum RESULTS {
        OK,
        WARNING,
        ERROR
    }
}
