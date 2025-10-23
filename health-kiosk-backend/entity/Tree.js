// Function will stored in Tree
class TreeNode {
  constructor(id, key, children = [], parent = null) {
    this.id = id
    this.key = key
    this.children = children
    this.parent = parent
  }

  addChild(node) {
    node.parent = this
    this.children.push(node)
  }

  removeChild(id) {
    const index = this.children.findIndex(c => c.id === id)
    if (index !== -1) {
      this.children[index].parent = null
      return this.children.splice(index, 1)[0]
    }
    for (const child of this.children) {
      const removed = child.removeChild(id)
      if (removed) return removed
    }
    return null
  }

  findById(id) {
    if (this.id === id) return this
    for (const child of this.children) {
      const found = child.findById(id)
      if (found) return found
    }
    return null
  }

  findByName(key) {
    if (this.key === key) return this
    for (const child of this.children) {
      const found = child.findByName(key)
      if (found) return found
    }
    return null
  }

  moveTo(newParent) {
    if (this.parent) {
      this.parent.removeChild(this.id)
    }
    newParent.addChild(this)
  }

  // 扁平化输出为数组
  flatten() {
    const result = []
    const dfs = (node) => {
      result.push({ id: node.id, key: node.key, parentId: node.parent ? node.parent.id : null })
      node.children.forEach(child => dfs(child))
    }
    dfs(this)
    return result
  }

  // 层级打印
  print(indent = 0) {
    console.log(`${' '.repeat(indent)}- ${this.key} (id:${this.id})`)
    this.children.forEach(child => child.print(indent + 2))
  }

  // 深拷贝
  clone() {
    const newNode = new TreeNode(this.id, this.key)
    newNode.children = this.children.map(child => {
      const c = child.clone()
      c.parent = newNode
      return c
    })
    return newNode
  }

  // 序列化为纯 JSON
  toJSON() {
    return {
      id: this.id,
      name: this.key,
      children: this.children.map(c => c.toJSON())
    }
  }
}
