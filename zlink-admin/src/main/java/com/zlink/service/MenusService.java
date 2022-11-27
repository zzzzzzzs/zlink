package com.zlink.service;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.zlink.dao.MenusMapper;
import com.zlink.entity.JobSysMenus;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 运营后台用户表 服务实现类
 * </p>
 *
 * @author zs
 * @since 2022-11-26
 */
@Service
@RequiredArgsConstructor
public class MenusService extends ServiceImpl<MenusMapper, JobSysMenus> {

    private final MenusMapper menusMapper;

    public List<Tree<Object>> listMenus() {
        List<JobSysMenus> menus = menusMapper.listMenus();
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setParentIdKey("parent_id");
        treeNodeConfig.setIdKey("id");
        treeNodeConfig.setNameKey("name");
        treeNodeConfig.setWeightKey("sort");
        treeNodeConfig.setDeep(2);

        List<Tree<Object>> treeNodes = TreeUtil.build(menus, 0, treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getParentId());
                    tree.setWeight(treeNode.getSort());
                    tree.setName(treeNode.getName());
                    tree.putExtra("path", treeNode.getPath());
                });

        return treeNodes;
    }
}
