package com.baron.acalculator


import android.util.Log

fun isdigit(num_str:String): Boolean {
    try{
        num_str.toDouble()
        return true
    }
    catch (e: Exception){
        return false
    }
    return true
}


object Expression{
    var iscomma: Boolean = false

    fun reset(){
        iscomma = false
    }

    fun add_to_exprstr(expr_str: String, symbol: Any): String{
        var symb: Char
        var expr = expr_str

        if (symbol is String){
            if (symbol.length == 0){ return expr }
            symb = symbol[0]
        }
        else if (symbol is Char){
            symb = symbol
        }
        else return expr

        if (symb == ','){
            symb = '.'
        }

        if (expr == "" && symb in "+/*."){
            return expr
        }
        else if (expr == "" && (symb.isDigit() || symb == '-')){ expr += symb }
        else if (expr.length == 1 && expr == "-" && !symb.isDigit()) { return expr }
        else{
            if (iscomma && symb == '.'){ return expr }
            else if (!iscomma && expr.last().isDigit() && symb == '.') {
                iscomma = true
                expr += symb
            }
            else if (expr.last() == '/' && symb == '0') { return expr }
            else if (symb.isDigit()) { expr += symb }
            else if (expr.last().isDigit() && symb in "+-*/") { expr += symb; iscomma = false }
            else if (expr.last() in "+-*/" && symb in "+*/") {
                expr = expr.substring(0, expr.lastIndex) + symb
            }
            else if (expr.last() in "+-*/" && expr[expr.lastIndex-1].isDigit() && symb == '-') { expr += symb; iscomma = false }
        }
        Log.d("STRINGPROC", "Return expr: ${expr}")
        return expr
    }

    fun parse(expr: String): MutableList<String>?{
        var res_expr: MutableList<String> = mutableListOf()
        var temp: String = ""

        if (expr == "") { return null }
        if (expr.length == 1 && expr == "-") {return null}
        if (expr.length == 1) {res_expr.add(expr)}
        else{
            for (i in 0..expr.lastIndex){
                if (expr[i].isDigit() || i == 0 && expr[i] == '-'){
                    temp += expr[i]
                }
                else if (expr[i] in "+-/*" && expr[i-1].isDigit()){
                    res_expr.add(temp)
                    res_expr.add(expr[i].toString())
                    temp = ""
                }
                else{ temp += expr[i] }
            }
        }

        if (temp != ""){
            res_expr.add(temp)
        }
        Log.d("STRINGPROC", "Return expr_list: ${res_expr}")
        return res_expr
    }

    fun calc_expr(expr: MutableList<String>): Double?{
        var i: Int = 0
        var op: String
        var a:Double;
        var b:Double;
        var c:Double?
        val operations = mapOf<String, (Double, Double) -> Double?>(
            "+" to {x:Double, y:Double -> x + y},
            "-" to {x:Double, y:Double -> x - y},
            "*" to {x:Double, y:Double -> x * y},
            "/" to {x:Double, y:Double -> if (y != 0.0) x / y else null}
        )


        if (expr.last() in "+-*/.,"){
            expr.removeAt(expr.lastIndex)
        }

        while (true){
            if (i >= expr.lastIndex){ break }
            op = expr[i]
            if (op in "*/"){
                a = expr[i-1].toDouble()
                b = expr[i+1].toDouble()
                c = operations.get(op)?.invoke(a, b)
                if (c == null) return null
                expr[i-1] = c.toString()
                expr.removeAt(i)
                expr.removeAt(i)
                i -= 1
            }
            i += 1
        }
        i = 0

        while (expr.size > 1){
            if (i >= expr.lastIndex){ break }
            op = expr[i]
            if (op in "+-"){
                a = expr[i-1].toDouble()
                b = expr[i+1].toDouble()
                c = operations.get(op)?.invoke(a, b)
                if (c == null) return null
                expr[i-1] = c.toString()
                expr.removeAt(i)
                expr.removeAt(i)
                i -= 1
            }
            i += 1
        }

        return expr[0].toDouble()
    }


}


















