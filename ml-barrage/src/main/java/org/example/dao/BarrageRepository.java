package org.example.dao;

import org.example.es.BarrageDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-19 15:26
 */
public interface BarrageRepository extends ElasticsearchRepository<BarrageDoc, Long> {

}
