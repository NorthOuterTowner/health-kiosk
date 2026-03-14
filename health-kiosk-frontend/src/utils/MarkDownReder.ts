import MarkdownIt from 'markdown-it';

const md = new MarkdownIt({
  html: true,        // 允许 HTML 标签
  linkify: true,     // 自动转换链接
  typographer: true,
});

export const renderMarkdown = (text: string) => {
  return md.render(text);
};