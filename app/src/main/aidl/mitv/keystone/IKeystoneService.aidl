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
package mitv.keystone;

import mitv.keystone.KeystonePoint;

/**
 * Interface of IKeystoneService
 * {@hide}
 */
interface IKeystoneService {
          int sendRawCmd(int param);
          int SetKeystoneInit();
          int SetKeystoneSave();
          int SetKeystoneLoad();
          int SetKeystoneLoadByMode(int mode);
          int SetKeystoneCancel();
          int SetKeystoneReset();
          int SetKeystoneSet(int select,int dir,int step);
          int SetKeystoneSelectMode(int mode);
          int GetKeystoneSelectMode();
          int GetKeystoneTimingMode();
          int SetKeystone3DFormat(int format);
          int SetKeystoneAutoCorrect(int mode);
          int GetKeystoneAutoCorrect();
          int SetKeystoneSets(in KeystonePoint[] pt);
          KeystonePoint[] GetKeystoneSets();
}
