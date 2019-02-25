/*
 * Copyright (C) 2008 The Android Open Source Project
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
package mitv.projector;

import mitv.projector.ProjectorInfo;

/**
 * Interface of IProjectorService
 * {@hide}
 */
interface IProjectorService {
    ProjectorInfo GetProjectorInfo();
    boolean SetDisplayImageOrientation(int orient);
    int GetDisplayImageOrientation();
    boolean SetDisplayImageBrightness(int brightness);
    boolean SetDisplayImageBrightnessMax(int brightness);
    boolean SetDisplayImageBrightnessSafety(int brightness);
    int GetDisplayImageBrightness();
    boolean SetDisplayImageSize(int h,int v);
    boolean SetAutoBrightnessByIR(int enable);
    int GetAutoBrightnessByIR();
    int GetProjectorEventStatus(int type);
    boolean SetProjectorTitlAngle(int degree);
    int GetProjectorTitlAngle();
}
