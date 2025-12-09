import express from 'express';
const router = express.Router();
import { db, genid } from '../db/dbUtils.js';
import User from '../entity/User.js';
import crypto from 'crypto';
import { v4 as uuidv4 } from 'uuid';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import { generateToken } from '../utils/jwtHelper.js';
import authMiddleware from '../middleware/authMiddleware.js'
import redisClient from '../db/redis.js';

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/'); // store catalog
    },
    filename: function (req, file, cb) {
        // 使用原始扩展名
        const ext = path.extname(file.originalname) || '.jpg';
        // 可以用 timestamp 或 uuid 保证唯一性
        cb(null, `${Date.now()}${ext}`);
    }
});

const upload = multer({ storage: storage });

/**
 * @route POST /news/create
 * @description 创建新新闻 (默认状态: draft/草稿)
 */
router.post('/create', async (req, res) => {
    try {
        const { publish, content } = req.body;

        if (!publish || !content) {
            return res.json({
                code: 400,
                msg: '发布者和内容不能为空'
            });
        }
        
        // 数据库 `id` 是 AUTO_INCREMENT，不再使用 genid
        // const id = genid.NextId();

        // status 默认设为 'draft'
        const sql = "INSERT INTO `news` (`publish`, `content`, `status`) VALUES (?, ?, ?)";
        const params = [publish, content, 'draft'];

        // 注意：db.async.run 返回 result, 包含 insertId
        const { err, result } = await db.async.run(sql, params); 

        if (err) {
            return res.json({
                code: 500,
                msg: '数据库错误',
                err
            })
        }

        return res.status(200).json({
            code: 200,
            msg: '新闻创建成功，状态为草稿',
            // 使用 insertId 获取自增主键
            data: { id: result.insertId } 
        });
    } catch (error) {
        // 捕获服务器运行异常
        console.error('Server Exception:', error);
        return res.status(200).json({
            code: 500,
            msg: '服务器异常',
            error
        });
    }
});

// TODO: UPDATE / EDIT (内容更新)
/**
 * @route POST /news/edit
 * @description 编辑新闻的内容和发布者
 */
router.post('/edit', async (req, res) => {
    try {
        const { id, publish, content } = req.body;

        if (!id || !publish || !content) {
            return res.json({
                code: 400,
                msg: 'ID、发布者和内容不能为空'
            });
        }

        // 更新内容、发布者，并更新 update_time
        const sql = "UPDATE `news` SET `publish` = ?, `content` = ?, `update_time` = CURRENT_TIMESTAMP WHERE `id` = ?";
        const params = [publish, content, id];

        const { err, result } = await db.async.run(sql, params);

        if (err) {
            return res.json({
                code: 500,
                msg: '数据库错误',
                err
            });
        }

        // 检查是否有行受到影响
        if (result.affectedRows === 0) {
            return res.status(200).json({
                code: 404,
                msg: '新闻不存在或内容未变化'
            });
        }

        return res.status(200).json({
            code: 200,
            msg: '新闻内容更新成功'
        });
    } catch (error) {
        console.error('Server Exception:', error);
        return res.status(200).json({
            code: 500,
            msg: '服务器异常',
            error
        });
    }
});

// TODO: PUBLISH (状态变更)
/**
 * @route POST /news/publish
 * @description 更改新闻的发布状态 ('publish' 或 'draft')
 */
router.post('/publish', async (req, res) => {
    try {
        // 接收 ID 和目标状态
        const { id, status } = req.body; 

        if (!id || (status !== 'draft' && status !== 'publish')) {
            return res.json({
                code: 400,
                msg: 'ID和有效状态 (draft/publish) 不能为空'
            });
        }
        
        let sql, params;
        
        if (status === 'publish') {
            // 如果是发布，除了更新 status，也更新 publish_time 和 update_time
            sql = "UPDATE `news` SET `status` = ?, `publish_time` = CURRENT_TIMESTAMP, `update_time` = CURRENT_TIMESTAMP WHERE `id` = ?";
            params = [status, id];
        } else {
            // 如果是设置为草稿，只更新 status 和 update_time
            sql = "UPDATE `news` SET `status` = ?, `update_time` = CURRENT_TIMESTAMP WHERE `id` = ?";
            params = [status, id];
        }


        const { err, result } = await db.async.run(sql, params);

        if (err) {
            return res.json({
                code: 500,
                msg: '数据库错误',
                err
            });
        }

        if (result.affectedRows === 0) {
            return res.status(200).json({
                code: 404,
                msg: '新闻不存在或状态未变化'
            });
        }
        
        const statusMsg = status === 'publish' ? '已发布' : '已设置为草稿';
        return res.status(200).json({
            code: 200,
            msg: `新闻状态更新成功: ${statusMsg}`
        });
    } catch (error) {
        console.error('Server Exception:', error);
        return res.status(200).json({
            code: 500,
            msg: '服务器异常',
            error
        });
    }
});

// TODO: DELETE
/**
 * @route POST /news/delete
 * @description 根据 ID 删除新闻
 */
router.post('/delete', async (req, res) => {
    try {
        const { id } = req.body;

        if (!id) {
            return res.json({
                code: 400,
                msg: '新闻ID不能为空'
            });
        }

        const sql = "DELETE FROM `news` WHERE `id` = ?";
        const params = [id];

        const { err, result } = await db.async.run(sql, params);

        if (err) {
            return res.json({
                code: 500,
                msg: '数据库错误',
                err
            });
        }

        if (result.affectedRows === 0) {
            return res.status(200).json({
                code: 404,
                msg: '新闻不存在'
            });
        }

        return res.status(200).json({
            code: 200,
            msg: '新闻删除成功'
        });
    } catch (error) {
        console.error('Server Exception:', error);
        return res.status(200).json({
            code: 500,
            msg: '服务器异常',
            error
        });
    }
});

// TODO: GETALL
/**
 * @route GET /news/list
 * @description 获取新闻列表 (支持按状态过滤、分页)
 */
router.get('/list', async (req, res) => {
    try {
        // 从查询参数获取分页和过滤条件
        const page = parseInt(req.query.page) || 1;
        const pageSize = parseInt(req.query.pageSize) || 10;
        const status = req.query.status; // 'draft' 或 'publish'
        const offset = (page - 1) * pageSize;
        
        let countSql = "SELECT COUNT(*) as total FROM `news`";
        let listSql = "SELECT `id`, `publish`, `publish_time`, `update_time`, `status` FROM `news`";
        let params = [];

        // 添加状态过滤条件
        if (status && (status === 'draft' || status === 'publish')) {
            const whereClause = " WHERE `status` = ?";
            countSql += whereClause;
            listSql += whereClause;
            params.push(status);
        }

        // 添加排序和分页
        listSql += " ORDER BY `publish_time` DESC LIMIT ?, ?";
        
        // 列表查询的参数：过滤参数 + offset + pageSize
        const listParams = [...params, offset, pageSize]; 
        const countParams = params; // 总数查询的参数：只有过滤参数

        // 1. 查询总数
        const { rows: countRows, err: countErr } = await db.async.all(countSql, countParams);

        if (countErr) {
            return res.json({
                code: 500,
                msg: '数据库查询总数错误',
                err: countErr
            });
        }
        
        const total = countRows[0].total;

        // 2. 查询列表数据
        const { rows: listRows, err: listErr } = await db.async.all(listSql, listParams);

        if (listErr) {
            return res.json({
                code: 500,
                msg: '数据库查询列表错误',
                err: listErr
            });
        }

        return res.status(200).json({
            code: 200,
            msg: '查询成功',
            data: {
                page,
                pageSize,
                total,
                list: listRows || []
            }
        });
    } catch (error) {
        console.error('Server Exception:', error);
        return res.status(200).json({
            code: 500,
            msg: '服务器异常',
            error
        });
    }
});


// TODO: VIEWDETAIL
/**
 * @route GET /news/detail
 * @description 获取单条新闻详情 (包含内容 content)
 */
router.get('/detail', async (req, res) => {
    try {
        const { id } = req.query;

        if (!id) {
            return res.json({
                code: 400,
                msg: '新闻ID不能为空'
            });
        }

        // 查询所有字段
        const sql = "SELECT * FROM `news` WHERE `id` = ?";
        const params = [id];

        // db.async.all 返回的是数组，如果只取一条，用 [0]
        const { err, rows } = await db.async.all(sql, params); 

        if (err) {
            return res.json({
                code: 500,
                msg: '数据库错误',
                err
            });
        }

        const news = rows[0];

        if (!news) {
            return res.status(200).json({
                code: 404,
                msg: '新闻不存在'
            });
        }

        return res.status(200).json({
            code: 200,
            msg: '查询成功',
            data: news
        });
    } catch (error) {
        console.error('Server Exception:', error);
        return res.status(200).json({
            code: 500,
            msg: '服务器异常',
            error
        });
    }
});

export default router;