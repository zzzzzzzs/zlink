package work.sajor.zlink.manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.sajor.crap.core.web.WebController;
import work.sajor.zlink.common.dao.dao.JdbcDatasourceDao;

/**
 * <p>
 * 数据源管理
 * </p>
 *
 * @author Sajor
 * @since 2022-11-23
 */
@RestController
@RequestMapping("/datasource")
public class DataSourceController extends WebController<JdbcDatasourceDao> {
}
