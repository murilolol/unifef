/**
 * Disciplina: Estrutura de Dados I (4º Semestre)
 * Professor: Prof. Wesley Soares
 * Tema: Trabalho AV1 - Sistema de Atendimento de Clínica
 * Classe: ListaLigada
 */
public class ListaLigada<T> {
    private No<T> primeiro;
    private int tamanho;

    public ListaLigada() {
        this.primeiro = null;
        this.tamanho = 0;
    }

    public boolean estaVazia() {
        return this.primeiro == null;
    }

    public int getTamanho() {
        return this.tamanho;
    }

    public No<T> getPrimeiro() {
        return this.primeiro;
    }

    // 1. Adicionar paciente (no final da lista)
    public void adicionar(T elemento) {
        No<T> novoNo = new No<>(elemento);
        if (estaVazia()) {
            this.primeiro = novoNo;
        } else {
            No<T> atual = this.primeiro;
            while (atual.getProximo() != null) {
                atual = atual.getProximo();
            }
            atual.setProximo(novoNo);
        }
        this.tamanho++;
    }

    // 2. Chamar próximo paciente (remover o primeiro e retornar)
    public T chamarProximo() {
        if (estaVazia()) {
            return null;
        }
        T info = this.primeiro.getInformacao();
        this.primeiro = this.primeiro.getProximo();
        this.tamanho--;
        return info;
    }

    // 3. Cancelar uma consulta (procurar pelo número da consulta e remover)
    public boolean cancelarConsulta(int numeroConsulta) {
        if (estaVazia()) {
            return false;
        }

        // Caso especial: remoção do primeiro elemento
        Paciente pPrimeiro = (Paciente) this.primeiro.getInformacao();
        if (pPrimeiro.getNumeroConsulta() == numeroConsulta) {
            this.primeiro = this.primeiro.getProximo();
            this.tamanho--;
            return true;
        }

        No<T> anterior = this.primeiro;
        No<T> atual = this.primeiro.getProximo();

        while (atual != null) {
            Paciente pAtual = (Paciente) atual.getInformacao();
            if (pAtual.getNumeroConsulta() == numeroConsulta) {
                anterior.setProximo(atual.getProximo());
                this.tamanho--;
                return true;
            }
            anterior = atual;
            atual = atual.getProximo();
        }
        return false;
    }

    // 4. Consultar paciente (procurar pelo número da consulta)
    public Paciente buscarPorConsulta(int numeroConsulta) {
        No<T> atual = this.primeiro;
        while (atual != null) {
            Paciente p = (Paciente) atual.getInformacao();
            if (p.getNumeroConsulta() == numeroConsulta) {
                return p;
            }
            atual = atual.getProximo();
        }
        return null;
    }

    // Método utilitário para exibir a lista
    public void imprimir() {
        if (estaVazia()) {
            System.out.println("Fila vazia.");
            return;
        }
        No<T> atual = this.primeiro;
        int posicao = 1;
        while (atual != null) {
            System.out.println(posicao + "º - " + atual.getInformacao());
            atual = atual.getProximo();
            posicao++;
        }
    }
}
