package com.baron.acalculator

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


object Expression{

    var iscomma = false // флаг указывающий, что в числе нет или уже есть запятая
    var df = DecimalFormat("#.###", DecimalFormatSymbols(Locale.US))

    fun reset(){
        iscomma = false
    }

    fun addToExprstr(expr_in:String, symbol:Any): String{
        /*
        Добавление символа к строковому мат выражению
        возвращает новую строку с добавленным в конец символом,
        если подходит под условия, иначе возвращает новую строку
        без добавленного символа
        */
        var expr:String = expr_in
        var symb: Char
        when (symbol){
            is Char -> { symb = symbol }
            is String -> { symb = if (symbol != "") symbol[0] else return expr }
            else -> return expr
        }
        if (symb == ',') { symb = '.' }

        if (expr.length == 0) {
            if (symb.isDigit() || symb == '-'){
                expr += symb
            }
        } else if (expr.length == 1) {
            if (expr == "-" && symb.isDigit()){ expr += symb }
            else if (expr == "0" && symb.isDigit()) { expr = symb.toString() }
            else if (expr[0].isDigit() && symb !in "+-/*"){
                if (symb == '.') { iscomma = true }
                expr += symb
            } else if (expr[0].isDigit() && symb in "+-/*"){
                iscomma = false
                expr += symb
            }
        } else {
            val idx = expr.lastIndex
            if (expr[idx] == '.' && symb.isDigit()) { expr += symb }
            else if (expr[idx] == '0' && symb.isDigit() && !iscomma){
                expr = expr.substring(0, idx) + symb
            }
            else if (expr[idx].isDigit() && symb in "+-/*"){
                iscomma = false
                expr += symb
            } else if (expr[idx].isDigit() && symb == '.' && !iscomma){
                iscomma = true
                expr += symb
            } else if (expr[idx].isDigit() && symb.isDigit()){ expr += symb }
            else if (expr[idx] in "+-*/" && expr[idx-1].isDigit() && symb in "+/*"){
                expr = expr.substring(0, idx) + symb
            }
            else if (expr[idx] in "+-*/" && expr[idx-1].isDigit() && symb == '-'){ expr += symb }
            else if (expr[idx] == '-' && expr[idx-1] in "+-*/" && symb.isDigit()) { expr += symb }
            else if (expr[idx] in "+-*/" && symb.isDigit()) { expr += symb }
        }
        return expr
    }


    fun split(expr: String): MutableList<String>{
        /*
        * разбивает стоку с выражением на числа и знаки действия
        * возвращает пустой список или список из чисел и знаков
        * строка с выражением заведомо корректная должна быть
        * */
        val split_expr: MutableList<String> = mutableListOf()
        var temp: String = ""

        if (expr.length == 1 && expr[0].isDigit()){
            split_expr.add(expr)
        }
        else if (expr.length > 1) {
            for (i in 0..expr.lastIndex){
                if (temp == "" && expr[i] == '-'){
                    temp += expr[i]
                } else if (expr[i].isDigit() || expr[i] in ".,"){
                    temp += expr[i]
                } else if (expr[i] in "+-*/" && expr[i-1].isDigit()){
                    split_expr.add(temp)
                    split_expr.add(expr[i].toString())
                    temp = ""
                }
            }
        }

        if (temp.length > 0){ split_expr.add(temp) }
        return split_expr
    }


    fun calc(expr: MutableList<String>): String?{
        /*
        * расчет результата
        */
        var result: String? = ""
        var i: Int = 0
        var op: String = ""
        var temp_res: Double? = 0.0

        if (expr.size == 0){ return "" }

        val operations = mapOf<String, (Double, Double) -> Double?>(
            "+" to {x:Double, y:Double -> x + y},
            "-" to {x:Double, y:Double -> x - y},
            "*" to {x:Double, y:Double -> x * y},
            "/" to {x:Double, y:Double -> if (y != 0.0) x / y else null}
        )

        if (expr.last() in "+-/*.,"){
            expr.removeAt(expr.lastIndex)
        }
        // сначала выполняются умножение и деление
        while (true){
            if (i >= expr.lastIndex) break
            op = expr[i]
            if (op in "/*"){ // если знак действия умножить или разделить
                // выполняется действие с числами перед и после знака
                // если второе число ноль, а операция деление, то вернется null
                temp_res = operations.get(op)?.invoke(expr[i-1].toDouble(), expr[i+1].toDouble())
                if (temp_res == null) return null
                expr[i-1] = temp_res.toString() // результатом заменяется первое число
                expr.removeAt(i) // удаляется знак действия
                expr.removeAt(i) // удаляется второе число
                i -= 1
            }
            i += 1
        }
        i = 0

        // сложение и вычитание
        while (true){
            if (i >= expr.lastIndex) break
            op = expr[i]
            if (op in "+-"){
                temp_res = operations.get(op)?.invoke(expr[i-1].toDouble(), expr[i+1].toDouble())
                expr[i-1] = temp_res.toString()
                expr.removeAt(i)
                expr.removeAt(i)
                i -= 1
            }
            i += 1
        }
        return df.format(expr[0].toDouble())
    }
}

