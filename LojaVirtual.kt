/**
 * LISTA DE EXERCÍCIOS 01 - Engenharia de Software / Soluções Mobile
 * Sistema de Loja Virtual em Kotlin utilizando Orientação a Objetos (POO)
 */

// ---------------------------------------------------------------
// 1. Classe Produto
// ---------------------------------------------------------------
class Produto(
    val id: Int,
    val nome: String,
    val preco: Double,
    var estoque: Int
) {
    fun exibirDetalhes() {
        println("ID: $id | Produto: $nome | Preço: R$ %.2f".format(preco) + " | Estoque: $estoque")
    }
}

// ---------------------------------------------------------------
// 2. Classe Cliente
// ---------------------------------------------------------------
class Cliente(
    val id: Int,
    val nome: String,
    var saldo: Double
) {
    fun adicionarSaldo(valor: Double) {
        if (valor > 0) {
            saldo += valor
            println("Saldo recarregado com sucesso! Novo saldo de $nome: R$ %.2f".format(saldo))
        } else {
            println("Valor inválido para recarga.")
        }
    }
}

// ---------------------------------------------------------------
// 3. Classe CarrinhoDeCompras
// ---------------------------------------------------------------
class CarrinhoDeCompras {

    // Armazena o produto e a quantidade escolhida pelo cliente
    private val itens: MutableMap<Produto, Int> = mutableMapOf()

    fun adicionarProduto(produto: Produto, quantidade: Int) {
        if (quantidade <= 0) {
            println("Quantidade inválida para o produto ${produto.nome}.")
            return
        }
        if (quantidade > produto.estoque) {
            println("Estoque insuficiente para o produto ${produto.nome}.")
            return
        }

        val quantidadeAtual = itens.getOrDefault(produto, 0)
        itens[produto] = quantidadeAtual + quantidade
        println("${produto.nome} adicionado ao carrinho (quantidade: $quantidade).")
    }

    fun removerProduto(produto: Produto) {
        if (itens.containsKey(produto)) {
            itens.remove(produto)
            println("${produto.nome} removido do carrinho.")
        } else {
            println("O produto ${produto.nome} não está no carrinho.")
        }
    }

    fun exibirCarrinho() {
        if (itens.isEmpty()) {
            println("O carrinho está vazio.")
            return
        }
        println("----- Itens no Carrinho -----")
        for ((produto, quantidade) in itens) {
            val subtotal = produto.preco * quantidade
            println("${produto.nome} | Qtd: $quantidade | Subtotal: R$ %.2f".format(subtotal))
        }
        println("Total: R$ %.2f".format(calcularTotal()))
    }

    fun calcularTotal(): Double {
        return itens.entries.sumOf { (produto, quantidade) -> produto.preco * quantidade }
    }

    fun getItens(): Map<Produto, Int> = itens

    fun limparCarrinho() {
        itens.clear()
    }
}

// ---------------------------------------------------------------
// 4. Classe Loja
// ---------------------------------------------------------------
class Loja {

    private val produtos: MutableList<Produto> = mutableListOf()

    fun adicionarProdutoNaLoja(produto: Produto) {
        produtos.add(produto)
    }

    fun listarProdutos() {
        println("===== Produtos disponíveis na loja =====")
        for (produto in produtos) {
            produto.exibirDetalhes()
        }
    }

    fun finalizarCompra(cliente: Cliente, carrinho: CarrinhoDeCompras) {
        val total = carrinho.calcularTotal()

        // Regra: o cliente só pode comprar se tiver saldo suficiente
        if (cliente.saldo < total) {
            println("Compra cancelada: saldo insuficiente para ${cliente.nome}.")
            println("Saldo atual: R$ %.2f".format(cliente.saldo) + " | Total da compra: R$ %.2f".format(total))
            return
        }

        // Regra: ao finalizar a compra, o estoque de cada produto diminui em 1 unidade
        for ((produto, _) in carrinho.getItens()) {
            if (produto.estoque > 0) {
                produto.estoque -= 1
            }
        }

        cliente.saldo -= total
        carrinho.limparCarrinho()

        println("Compra finalizada com sucesso para ${cliente.nome}!")
        println("Valor pago: R$ %.2f".format(total) + " | Saldo restante: R$ %.2f".format(cliente.saldo))
    }
}

// ---------------------------------------------------------------
// Função principal - demonstração de uso do sistema
// ---------------------------------------------------------------
fun main() {

    // Criando a loja e os produtos
    val loja = Loja()
    val produto1 = Produto(1, "Notebook", 3500.00, 5)
    val produto2 = Produto(2, "Mouse", 80.00, 10)
    val produto3 = Produto(3, "Teclado", 150.00, 8)

    loja.adicionarProdutoNaLoja(produto1)
    loja.adicionarProdutoNaLoja(produto2)
    loja.adicionarProdutoNaLoja(produto3)

    loja.listarProdutos()
    println()

    // Criando um cliente
    val cliente = Cliente(1, "Maria Luíza", 1000.00)

    // Criando o carrinho e adicionando produtos
    val carrinho = CarrinhoDeCompras()
    carrinho.adicionarProduto(produto2, 2) // 2 mouses
    carrinho.adicionarProduto(produto3, 1) // 1 teclado

    println()
    carrinho.exibirCarrinho()
    println()

    // Finalizando a compra (saldo suficiente)
    loja.finalizarCompra(cliente, carrinho)
    println()

    // Testando saldo insuficiente
    carrinho.adicionarProduto(produto1, 1) // Notebook, mais caro que o saldo restante
    println()
    carrinho.exibirCarrinho()
    println()
    loja.finalizarCompra(cliente, carrinho)

    println()
    loja.listarProdutos()
}
