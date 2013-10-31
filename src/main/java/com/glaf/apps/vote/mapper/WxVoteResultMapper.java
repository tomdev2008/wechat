/*
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

package com.glaf.apps.vote.mapper;

import java.util.*;
import org.springframework.stereotype.Component;
import com.glaf.apps.vote.domain.*;
import com.glaf.apps.vote.query.*;

@Component
public interface WxVoteResultMapper {

	void deleteWxVoteResults(WxVoteResultQuery query);

	void deleteWxVoteResultById(Long id);

	void deleteWxVoteResultByVoteId(Long voteId);

	WxVoteResult getWxVoteResultById(Long id);
	
	WxVoteResult getLatestVoteResult(WxVoteResultQuery query);

	int getWxVoteResultCount(WxVoteResultQuery query);

	List<WxVoteResult> getWxVoteResultsByVoteId(Long voteId);

	List<WxVoteResult> getWxVoteResults(WxVoteResultQuery query);

	void insertWxVoteResult(WxVoteResult model);

	void updateWxVoteResult(WxVoteResult model);

}
