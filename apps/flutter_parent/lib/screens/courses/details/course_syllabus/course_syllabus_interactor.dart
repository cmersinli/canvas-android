// Copyright (C) 2020 - present Instructure, Inc.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, version 3 of the License.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

import 'package:flutter_parent/models/authenticated_url.dart';
import 'package:flutter_parent/network/api/oauth_api.dart';
import 'package:flutter_parent/network/utils/api_prefs.dart';
import 'package:flutter_parent/utils/service_locator.dart';

class CourseSyllabusInteractor {
  Future<String> getUrl(String url) {
    if (url.contains(ApiPrefs.getDomain())) {
      // Get an authenticated session so the user doesn't have to log in
      return getAuthedUrl(url).then((v) => v.sessionUrl);
    } else {
      return Future.value(url);
    }
  }

  Future<AuthenticatedUrl> getAuthedUrl(String url) => locator.get<OAuthApi>().getAuthenticatedUrl(url);
}