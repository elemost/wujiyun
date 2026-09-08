import { evaluate } from 'mathjs'
// 如果只有一个成员，同时第一个成员为 args
// 则把它展开，当成是多个参数，毕竟公式里面还不支持 ...args 语法，
function normalizeArgs(args) {
    if (args.length === 1 && Array.isArray(args[0])) {
        args = args[0];
    }
    return args;
}

/**
 * 格式化数字
 * @param {any} value 要格式化的值
 * @param {boolean} int 是否取整
 * @returns {number} 格式化后的数字
 */
function formatNumber(value, int = false) {
    const typeName = typeof value;
    if (typeName === 'string') {
        return (int ? parseInt(value, 10) : parseFloat(value)) || 0;
    } else if (typeName === 'number' && int) {
        return Math.round(value);
    }

    return value ?? 0;
}

/**
* 解析参数字符串
*/
function parseArguments(argsStr) {
    // 这里需要实现参数分割逻辑
    // 简单实现，实际需要处理嵌套括号等情况
    return argsStr.split(',').map(arg => {
        const num = parseFloat(arg);
        return isNaN(num) ? arg : num;
    });
}

/**
 * 公式函数
 * @type {Object}
 */
export const formula = {
    /**
  * 如果满足条件condition，则返回consequent，否则返回alternate，支持多层嵌套IF函数。
  *
  * 等价于直接用JS表达式如：condition ? consequent : alternate。
  *
  * @example IF(condition, consequent, alternate)
  * @param {expression} condition 条件表达式。例如：语文成绩>80
  * @param {any} consequent 条件判断通过的返回结果
  * @param {any} alternate 条件判断不通过的返回结果
  * @namespace 逻辑函数
  *
  * @returns {any} 根据条件返回不同的结果
  */
    fnIF: (condition, trueValue, falseValue) => {
        return condition() ? trueValue() : falseValue();
    },
    /**
   * 求和，如果只有一个参数且是数组，则计算这个数组内的值。
   *
   * @example SUM(num1, num2, ...numN) or SUM([num1, num2, ...numN])
   * @param {...number} num - 数值
   * @namespace 数学函数
   *
   * @returns {number} 所有传入数值的总和
   */
    fnSUM(...args) {
        const arr = normalizeArgs(args);
        return arr.reduce((sum, a) => sum + formatNumber(a) || 0, 0);
    }
}

/**
 * 解析并执行公式字符串
 * @param {string} formula 公式字符串
 * @returns {number} 计算结果
 */
export function evaluateFormula(formula) {
    try {
        // 使用math.js解析数学表达式
        return evaluate(formula)
    } catch (e) {
        // 如果math.js无法解析，尝试原有逻辑
        const funcMatch = formula.match(/^(\w+)\((.*)\)$/)
        if (!funcMatch) return formula

        const funcName = `fn${funcMatch[1]}`
        const argsStr = funcMatch[2]

        if (!formula[funcName]) return formula

        const args = parseArguments(argsStr)
        const processedArgs = args.map(arg => {
            if (typeof arg === 'string' && /^\w+\(.*\)$/.test(arg)) {
                return evaluateFormula(arg)
            }
            return arg
        })

        return formula[funcName](...processedArgs)
    }
}
