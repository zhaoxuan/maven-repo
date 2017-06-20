/*
* Copyright (C) 2016 JohnZ
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the Free
* Software Foundation, either version 3 of the License, or (at your option)
* any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along
* with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.qgrid;



/**
 * 公共代码的初始化方法
 *      其他所有共有代码需要执行初始的，都必须在这里执行
 */
public class CommonCore {
    private static CommonCore singleton;

    /**
     * 初始化方法.
     *
     * @return The value returned by the method
     *
     * @throws Exception What kind of exception does this method throw
     */
    public static synchronized CommonCore initial() {
        if (CommonCore.singleton == null) {
            CommonCore.singleton = new CommonCore();
        }

        return CommonCore.singleton;
    }

    public static CommonCore getInstance() {
        if (singleton == null) {
            throw new IllegalStateException("CommonCore has not been initial，please callback initial function.");
        }
        return singleton;
    }
}
