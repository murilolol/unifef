ria: number
    ) {}

    gerarPDF(): string {
        return `
======================================================================
                         CERTIFICADO DE PARTICIPAÇÃO
======================================================================
Certificamos que ${this.aluno.nome} (Matrícula: ${this.aluno.matricula})
participou com sucesso da atividade "${this.atividade.titulo}",
realizada em ${this.atividade.horario.toLocaleDateString()} com carga horária de ${this.cargaHoraria} horas.

Código de Autenticidade: ${this.codigoAutenticidade}
Data de Emissão: ${this.dataEmissao.toLocaleDateString()}
======================================================================
        `;