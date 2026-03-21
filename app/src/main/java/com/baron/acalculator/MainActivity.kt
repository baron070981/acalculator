package com.baron.acalculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.baron.acalculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var expr:String = ""
    private var exprList: MutableList<String> = mutableListOf()
    private var result: String? = ""

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_clear = binding.buttonRes // очистить ввод
        // числа от 1 до 0
        val btn_1 = binding.button1
        val btn_2 = binding.button2
        val btn_3 = binding.button3
        val btn_4 = binding.button4
        val btn_5 = binding.button5
        val btn_6 = binding.button6
        val btn_7 = binding.button7
        val btn_8 = binding.button8
        val btn_9 = binding.button9
        val btn_0 = binding.button0
        val btn_result = binding.buttonResult // результат
        val btn_plus = binding.buttonPlus // сложение
        val btn_del = binding.btnDel // удаление посимвольно с конца
        val btn_minus = binding.buttonMinus // вычитание
        val btn_mult = binding.buttonMult // умножение
        val btn_div = binding.buttonDiv // деление
        val btn_sep = binding.buttonSep // запятая

        val inpNums = binding.inputNumbers
        val outRes = binding.resultOut

        // обработка нажатий кнопок 0-9 и знаков действия
        val pressedBtn = View.OnClickListener{
            val text = (it as Button).text.toString()
            expr = Expression.addToExprstr(expr, text)
            inpNums.setText(expr)
            exprList = Expression.split(expr)
            Log.d("STRINGPROC", "Return expr in main: ${exprList}")
            result = Expression.calc(exprList)
            if (result == null) outRes.setText("На ноль делить нельзя")
            else outRes.setText(result)

        }

        // удаление символа с конца
        val deleteSymb = View.OnClickListener{
            if (expr.length > 0){
                if (expr.last() in ".,"){
                    Expression.reset()
                }
                expr = expr.substring(0, expr.lastIndex)
                inpNums.setText(expr)
                exprList = Expression.split(expr)
                result = Expression.calc(exprList)
                if (result == null) outRes.setText("На ноль делить нельзя")
                else outRes.setText(result)

                if (expr == "") {
                        outRes.setText("0")
                        Expression.reset()
                }
            }
        }

        // обработка нажатия на кнопку равно
        val getResult = View.OnClickListener{
            expr = result.toString()
            inpNums.setText(result)
            Expression.reset()
        }

        // обработка нажатия очистки ввода
        val clearData = View.OnClickListener{
            expr = ""
            inpNums.setText(expr)
            outRes.setText("0")
            Expression.reset()
        }

        btn_1.setOnClickListener(pressedBtn)
        btn_2.setOnClickListener(pressedBtn)
        btn_3.setOnClickListener(pressedBtn)
        btn_4.setOnClickListener(pressedBtn)
        btn_5.setOnClickListener(pressedBtn)
        btn_6.setOnClickListener(pressedBtn)
        btn_7.setOnClickListener(pressedBtn)
        btn_8.setOnClickListener(pressedBtn)
        btn_9.setOnClickListener(pressedBtn)
        btn_0.setOnClickListener(pressedBtn)
        btn_plus.setOnClickListener(pressedBtn)
        btn_minus.setOnClickListener(pressedBtn)
        btn_mult.setOnClickListener(pressedBtn)
        btn_div.setOnClickListener(pressedBtn)
        btn_sep.setOnClickListener(pressedBtn)
        btn_result.setOnClickListener(getResult)
        btn_clear.setOnClickListener(clearData)
        btn_del.setOnClickListener(deleteSymb)

    }
}