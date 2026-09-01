package com.unisatc.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    CalculadoraScreen()
                }
            }
        }
    }
}

// Cores personalizadas da calculadora
val RoxoPrincipal = Color(0xFF7E57C2)
val RoxoEscuro = Color(0xFF5E35B1)
val RoxoOperador = Color(0xFF9575CD)

@Composable
fun CalculadoraScreen() {
    // Estados que guardam o número atual, o número anterior e o operador escolhido
    var display by remember { mutableStateOf("0") }
    var numeroAnterior by remember { mutableStateOf(0.0) }
    var operadorAtual by remember { mutableStateOf<String?>(null) }
    var iniciandoNovoNumero by remember { mutableStateOf(false) }

    // Função chamada quando um número é pressionado
    fun onNumeroClick(numero: String) {
        display = if (display == "0" || iniciandoNovoNumero) {
            iniciandoNovoNumero = false
            numero
        } else {
            display + numero
        }
    }

    // Função chamada quando um operador (+, -, *, /) é pressionado
    fun onOperadorClick(operador: String) {
        numeroAnterior = display.toDouble()
        operadorAtual = operador
        iniciandoNovoNumero = true
    }

    // Função que realiza o cálculo quando "=" é pressionado
    fun onIgualClick() {
        val numeroAtual = display.toDouble()
        val resultado = when (operadorAtual) {
            "+" -> numeroAnterior + numeroAtual
            "-" -> numeroAnterior - numeroAtual
            "*" -> numeroAnterior * numeroAtual
            "/" -> if (numeroAtual != 0.0) numeroAnterior / numeroAtual else Double.NaN
            else -> numeroAtual
        }

        display = if (resultado == resultado.toLong().toDouble()) {
            resultado.toLong().toString()
        } else {
            resultado.toString()
        }

        operadorAtual = null
        iniciandoNovoNumero = true
    }

    // Função que limpa a calculadora (botão "C")
    fun onLimparClick() {
        display = "0"
        numeroAnterior = 0.0
        operadorAtual = null
        iniciandoNovoNumero = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Display da calculadora
        Text(
            text = display,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            fontSize = 48.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.End,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Linha 1: 1, 2, 3, +
        LinhaDeBotoes {
            BotaoCalculadora("1", RoxoPrincipal) { onNumeroClick("1") }
            BotaoCalculadora("2", RoxoPrincipal) { onNumeroClick("2") }
            BotaoCalculadora("3", RoxoPrincipal) { onNumeroClick("3") }
            BotaoCalculadora("+", RoxoOperador) { onOperadorClick("+") }
        }

        // Linha 2: 4, 5, 6, -
        LinhaDeBotoes {
            BotaoCalculadora("4", RoxoPrincipal) { onNumeroClick("4") }
            BotaoCalculadora("5", RoxoPrincipal) { onNumeroClick("5") }
            BotaoCalculadora("6", RoxoPrincipal) { onNumeroClick("6") }
            BotaoCalculadora("-", RoxoOperador) { onOperadorClick("-") }
        }

        // Linha 3: 7, 8, 9, *
        LinhaDeBotoes {
            BotaoCalculadora("7", RoxoPrincipal) { onNumeroClick("7") }
            BotaoCalculadora("8", RoxoPrincipal) { onNumeroClick("8") }
            BotaoCalculadora("9", RoxoPrincipal) { onNumeroClick("9") }
            BotaoCalculadora("*", RoxoOperador) { onOperadorClick("*") }
        }

        // Linha 4: C, 0, =, /
        LinhaDeBotoes {
            BotaoCalculadora("C", RoxoEscuro) { onLimparClick() }
            BotaoCalculadora("0", RoxoPrincipal) { onNumeroClick("0") }
            BotaoCalculadora("=", RoxoEscuro) { onIgualClick() }
            BotaoCalculadora("/", RoxoOperador) { onOperadorClick("/") }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Componente auxiliar para organizar 4 botões em uma linha, ocupando todo o espaço horizontal
@Composable
fun LinhaDeBotoes(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

// Componente de botão reutilizável, com formato arredondado (pill), como na imagem do exercício
@Composable
fun RowScope.BotaoCalculadora(
    texto: String,
    cor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(64.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = cor)
    ) {
        Text(
            text = texto,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
