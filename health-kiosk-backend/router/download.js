import express from 'express';
const router = express.Router();
import { db, genid } from '../db/dbUtils.js';
import fs from 'fs';
import authMiddleware from '../middleware/authMiddleware.js';
import XLSX from 'xlsx';
import { Document, Packer, Paragraph, Table, TableRow, TableCell, WidthType } from 'docx';

router.post("/data", authMiddleware, async (req, res) => {
    try {
        const user_id = req.account;

        // file_type<"csv" | "xlsx" | "docx">
        const {
            start_date,
            end_date,
            file_type
        } = req.body;

        if (!start_date || !end_date) {
            return res.status(400).json({
                code: 400,
                msg: "缺少时间范围参数"
            });
        }

        const select_sql = `
            SELECT *
            FROM \`data\`
            WHERE \`user_id\` = ?
              AND \`date\` BETWEEN ? AND ?
            ORDER BY \`id\`
        `;

        const { rows, err } = await db.async.all(select_sql, [
            user_id,
            start_date,
            end_date
        ]);

        if (err) {
            return res.status(500).json({
                code: 500,
                msg: err.msg
            });
        }

        if (!rows || rows.length === 0) {
            return res.status(200).json({
                code: 200,
                msg: "无可导出的数据"
            });
        }

        if(file_type === "csv") {
            const headers = Object.keys(rows[0]).join(",");

            const csvBody = rows.map(row =>
                Object.values(row)
                    .map(v => `"${v ?? ""}"`)
                    .join(",")
            ).join("\n");

            const csvContent = headers + "\n" + csvBody;

            const filename = `data_${start_date}_${end_date}.csv`;

            res.setHeader("Content-Type", "text/csv; charset=utf-8");
            res.setHeader(
                "Content-Disposition",
                `attachment; filename=${encodeURIComponent(filename)}`
            );

            res.send(csvContent);
        }else if(file_type === "xlsx") {
            const worksheet = XLSX.utils.json_to_sheet(rows);
            const workbook = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(workbook, worksheet, "Health Data");
            
            const excelBuffer = XLSX.write(workbook, { type: 'buffer', bookType: 'xlsx' });
            const filename = `data_${start_date}_${end_date}.xlsx`;

            // 响应头
            res.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", `attachment; filename=${encodeURIComponent(filename)}`);
            
            return res.send(excelBuffer);
        }else if(file_type === "docx") {
            const headers = Object.keys(rows[0]);
    
            const tableHeader = new TableRow({
                children: headers.map(header => new TableCell({
                    children: [new Paragraph({ text: header, bold: true })],
                    backgroundColor: "#f3f3f3"
                })),
            });

            const dataRows = rows.map(row => new TableRow({
                children: Object.values(row).map(val => new TableCell({
                    children: [new Paragraph({ text: String(val ?? "") })],
                })),
            }));

            const doc = new Document({
                sections: [{
                    properties: {},
                    children: [
                        new Paragraph({ text: "Self-Examination Data Report", heading: "Heading1" }),
                        new Paragraph({ text: `Export Date Range: ${start_date} to ${end_date}\n` }),
                        new Table({
                            rows: [tableHeader, ...dataRows],
                            width: { size: 100, type: WidthType.PERCENTAGE }
                        }),
                    ],
                }],
            });

            const docBuffer = await Packer.toBuffer(doc);
            const filename = `data_${start_date}_${end_date}.docx`;

            res.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            res.setHeader("Content-Disposition", `attachment; filename=${encodeURIComponent(filename)}`);
            
            return res.send(docBuffer);
        }

    } catch (e) {
        res.status(500).json({
            code: 500,
            msg: "服务器内部错误"
        });
    }
});

export default router;