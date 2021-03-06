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

package com.glaf.wechat.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.core.config.SystemProperties;
import com.glaf.core.dao.EntityDAO;
import com.glaf.core.id.IdGenerator;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.UUID32;
import com.glaf.wechat.domain.WxFile;
import com.glaf.wechat.mapper.WxFileMapper;
import com.glaf.wechat.query.WxFileQuery;
import com.glaf.wechat.service.WxFileService;

@Service("wxFileService")
@Transactional(readOnly = true)
public class WxFileServiceImpl implements WxFileService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected WxFileMapper wxFileMapper;

	public WxFileServiceImpl() {

	}

	public int count(WxFileQuery query) {
		return wxFileMapper.getWxFileCount(query);
	}

	@Transactional
	public void deleteById(Long id) {
		if (id != null) {
			wxFileMapper.deleteWxFileById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<Long> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (Long id : ids) {
				wxFileMapper.deleteWxFileById(id);
			}
		}
	}

	/**
	 * 删除某个栏目的文件
	 * 
	 * @param categoryId
	 */
	@Transactional
	public void deleteByCategoryId(Long categoryId) {
		WxFileQuery query = new WxFileQuery();
		query.categoryId(categoryId);
		List<WxFile> rows = list(query);
		if (rows != null && !rows.isEmpty()) {
			for (WxFile file : rows) {
				FileUtils.deleteFile(SystemProperties.getAppPath()
						+ file.getPath());
				wxFileMapper.deleteWxFileById(file.getId());
			}
		}
	}

	public WxFile getWxFile(Long id) {
		if (id == null) {
			return null;
		}
		WxFile wxFile = wxFileMapper.getWxFileById(id);
		return wxFile;
	}

	public WxFile getWxFileByUUID(String uuid) {
		return wxFileMapper.getWxFileByUUID(uuid);
	}

	public int getWxFileCountByQueryCriteria(WxFileQuery query) {
		return wxFileMapper.getWxFileCount(query);
	}

	public List<WxFile> getWxFilesByQueryCriteria(int start, int pageSize,
			WxFileQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<WxFile> rows = sqlSessionTemplate.selectList("getWxFiles", query,
				rowBounds);
		return rows;
	}

	public List<WxFile> list(WxFileQuery query) {
		List<WxFile> list = wxFileMapper.getWxFiles(query);
		return list;
	}

	@Transactional
	public void save(WxFile wxFile) {
		if (wxFile.getId() == 0) {
			wxFile.setId(idGenerator.nextId());
			wxFile.setCreateDate(new Date());
			if (wxFile.getUuid() == null) {
				wxFile.setUuid(UUID32.getUUID());
			}
			wxFileMapper.insertWxFile(wxFile);
		} else {
			wxFile.setLastUpdateDate(new Date());
			wxFileMapper.updateWxFile(wxFile);
		}
	}

	@Transactional
	public void saveAll(List<WxFile> files) {
		for (WxFile file : files) {
			this.save(file);
		}
	}

	@javax.annotation.Resource
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@javax.annotation.Resource
	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@javax.annotation.Resource
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@javax.annotation.Resource
	public void setWxFileMapper(WxFileMapper wxFileMapper) {
		this.wxFileMapper = wxFileMapper;
	}

}
