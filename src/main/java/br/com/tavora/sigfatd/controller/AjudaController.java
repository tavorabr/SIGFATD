package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.view.TelaAjudaView;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class AjudaController {

    private final TelaAjudaView view;
    private final Map<String, String> conteudoAjuda;

    public AjudaController(TelaAjudaView view) {
        this.view = view;
        this.conteudoAjuda = new LinkedHashMap<>();
        initController();
    }

    private void initController() {
        carregarConteudo();
        Vector<String> topicos = new Vector<>(conteudoAjuda.keySet());
        view.popularTopicos(topicos);

        view.adicionarAcaoSelecaoTopico(e -> {
            if (!e.getValueIsAdjusting()) {
                String topicoSelecionado = view.getTopicoSelecionado();
                if (topicoSelecionado != null) {
                    view.setTextoAjuda(conteudoAjuda.get(topicoSelecionado));
                }
            }
        });

        if (!topicos.isEmpty()) {
            view.setTextoAjuda(conteudoAjuda.get(topicos.firstElement()));
        }
    }

    private void carregarConteudo() {

        conteudoAjuda.put("Visão Geral",
                "<html>" +
                        "<h1>Bem-vindo ao SIGFATD!</h1>" +
                        "<p>Este é o Sistema de Gestão de Formulários de Apuração de Transgressão Disciplinar.</p>" +
                        "<p>O objetivo deste sistema é centralizar, agilizar e padronizar todo o processo de apuração disciplinar, " +
                        "desde o registro do fato até a decisão final da autoridade competente, garantindo o controle de prazos e a gestão " +
                        "dos documentos gerados.</p>" +
                        "<p>Use o menu à esquerda para navegar pelos tópicos de ajuda e aprender a usar cada funcionalidade do sistema.</p>" +
                        "</html>"
        );

        conteudoAjuda.put("Níveis de Acesso",
                "<html>" +
                        "<h3>Entendendo os Níveis de Acesso</h3>" +
                        "<p>O SIGFATD possui quatro níveis de acesso, cada um com permissões específicas para garantir a segurança e a correta " +
                        "distribuição de tarefas no processo disciplinar.</p><hr>" +
                        "<h4>1. MASTER</h4>" +
                        "<p><b>Função:</b> Administrador total do sistema.</p>" +
                        "<p><b>Permissões:</b> Acesso a <b>todas</b> as funcionalidades sem restrições. É o único que pode gerenciar os usuários" +
                        " do sistema e excluir processos de FATD.</p>" +
                        "<p><b>Por quê?</b> Este perfil é destinado para dar manutenção e testes no sistema.</p><hr>" +
                        "<h4>2. PARTICIPANTE</h4>" +
                        "<p><b>Função:</b> Operador principal do sistema, responsável por registrar e acompanhar os processos.</p>" +
                        "<p><b>Permissões:</b> Pode gerenciar e importar militares, gerar FATDs, controlar prazos e anexar documentos. Não pode " +
                        "gerenciar usuários nem dar a decisão final de punição.</p>" +
                        "<p><b>Por quê?</b> Este perfil é ideal para o militar encarregado de confeccionar as FATDs e organizar a documentação. " +
                        "Ele prepara todo o processo para que a autoridade competente possa julgá-lo.</p><hr>" +
                        "<h4>3. AUTORIDADE</h4>" +
                        "<p><b>Função:</b> Perfil de Julgador. Comandante Cia ou de OM.</p>" +
                        "<p><b>Permissões:</b> Acesso focado na análise e decisão dos processos. Pode visualizar o arquivo de FATDs, controlar os" +
                        " prazos e, principalmente, acessar a tela de 'Decisão de Punição' para registrar sua solução.</p>" +
                        "<p><b>Por quê?</b> Isola a função de julgamento. A Autoridade não precisa se envolver na criação dos processos, apenas na" +
                        " sua análise e decisão final, garantindo a separação de responsabilidades.</p><hr>" +
                        "<h4>4. INTELIGÊNCIA</h4>" +
                        "<p><b>Função:</b> Perfil de Consulta e Análise.</p>" +
                        "<p><b>Permissões:</b> Acesso de apenas leitura ao arquivo de FATDs e ao controle de prazos. Não pode criar, editar ou " +
                        "excluir nada.</p>" +
                        "<p><b>Por quê?</b> Destinado a setores que precisam de acesso aos dados para fins de análise, estatística ou inteligência," +
                        " sem o risco de alterarem qualquer informação dos processos.</p>" +
                        "</html>"
        );

        conteudoAjuda.put("Importar Militares",
                "<html>" +
                        "<h3>Como Importar Militares via CSV</h3>" +
                        "<p>A importação de militares permite cadastrar em massa todos os militares da sua unidade de uma só vez a partir de um " +
                        "arquivo CSV.</p>" +
                        "<p><b>Atenção:</b> A importação <b>adiciona apenas os novos militares</b>. Se um militar com a mesma identidade já " +
                        "existir no sistema, ele será ignorado para evitar duplicatas.</p>" +
                        "<h4>Padrão do Arquivo</h4>" +
                        "<p>O arquivo CSV deve seguir a seguinte estrutura de colunas, nesta ordem:</p>" +
                        "<ol>" +
                        "<li><b>idt_militar:</b> A identidade militar (apenas números).</li>" +
                        "<li><b>posto_grad:</b> O posto ou graduação (ex: '3º Sgt', 'Cb', 'Sd EP').</li>" +
                        "<li><b>nome_completo:</b> O nome completo do militar.</li>" +
                        "<li><b>nome_guerra:</b> O nome de guerra do militar.</li>" +
                        "</ol>" +
                        "<b>Detalhes Técnicos:</b>" +
                        "<ul>" +
                        "<li>O arquivo deve ter um cabeçalho na primeira linha (será ignorado pelo sistema).</li>" +
                        "<li>O separador de colunas pode ser vírgula (,) ou ponto e vírgula (;).</li>" +
                        "<li>O arquivo deve estar no formato de codificação <b>UTF-8</b> para evitar problemas com acentos.</li>" +
                        "</ul>" +
                        "</html>"
        );

        conteudoAjuda.put("Importar NUP",
                "<html>" +
                        "<h3>Como Importar NUPs via CSV</h3>" +
                        "<p>Esta funcionalidade permite associar em massa os Números Únicos de Protocolo (NUP) aos números sequenciais de " +
                        "FATD utilizados pelo sistema.</p>" +
                        "<h4>Padrão do Arquivo</h4>" +
                        "<p>O arquivo CSV deve ter <b>exatamente duas colunas</b>, nesta ordem:</p>" +
                        "<ol>" +
                        "<li><b>numero_fatd:</b> O número sequencial da FATD (ex: 1, 2, 3...).</li>" +
                        "<li><b>nup_completo:</b> O NUP completo correspondente, em formato de texto.</li>" +
                        "</ol>" +
                        "<p>Ao gerar uma FATD, o sistema usará o próximo número sequencial e buscará nesta lista o NUP correspondente para " +
                        "preencher o documento automaticamente.</p>" +
                        "<b>Detalhes Técnicos:</b>" +
                        "<ul>" +
                        "<li>O arquivo deve ter um cabeçalho na primeira linha (será ignorado).</li>" +
                        "<li>O separador pode ser vírgula (,) ou ponto e vírgula (;).</li>" +
                        "<li>O arquivo deve estar no formato <b>UTF-8</b>.</li>" +
                        "</ul>" +
                        "</html>"
        );

        conteudoAjuda.put("Gerar FATD",
                "<html>" +
                        "<h3>Como Gerar uma Nova FATD</h3>" +
                        "<p>Esta é a função principal para registrar uma nova transgressão disciplinar.</p>" +
                        "<ol>" +
                        "<li><b>Selecionar Militar:</b> Na lista da esquerda, clique sobre o militar transgressor. Você pode usar a barra de" +
                        " pesquisa acima da lista para encontrá-lo rapidamente pelo nome completo ou de guerra.</li>" +
                        "<li><b>Preencher Referência:</b> No campo 'Referência', descreva o documento ou fato que originou a apuração " +
                        "(ex: 'Parte do S Ten Fulano', 'Alterações do Cb Ciclano').</li>" +
                        "<li><b>Relatar os Fatos:</b> No campo 'Relato dos Fatos', descreva de forma clara e objetiva a ocorrência que" +
                        " constitui a transgressão.</li>" +
                        "<li><b>Selecionar Participante:</b> No menu 'Militar Participante', selecione o militar que está formalizando a parte.</li>" +
                        "<li><b>Gerar Documento:</b> Clique no botão <b>'Gerar FATD'</b>.</li>" +
                        "<li>O sistema irá gerar o documento .docx na pasta 'Documentos/SIGFATD_Arquivos' (dentro de uma subpasta com o número" +
                        " da FATD) e o processo será registrado no sistema para controle de prazos.</li>" +
                        "</ol>" +
                        "</html>"
        );

        // TÓPICO NOVO ADICIONADO ABAIXO
        conteudoAjuda.put("Arquivo de FATDs",
                "<html>" +
                        "<h3>Consultando e Gerenciando Processos Arquivados</h3>" +
                        "<p>A tela 'Arquivo de FATDs' é o repositório central de todos os processos gerados pelo sistema. A partir daqui, você" +
                        " pode consultar, anexar documentos e gerenciar os processos antigos.</p>" +
                        "<h4>Funcionalidades:</h4>" +
                        "<ul>" +
                        "<li><b>Anexar Arquivo:</b> Esta é uma função crucial para manter o histórico completo do processo. Após o documento da" +
                        " FATD ou da Decisão ser assinado, você deve escaneá-lo (ou usar a versão em PDF) e anexá-lo aqui. Isso garante que o " +
                        "documento assinado fique permanentemente guardado junto com o processo no sistema para consultas futuras.</li>" +
                        "<li><b>Abrir Arquivo:</b> Permite visualizar qualquer documento que esteja anexado a uma FATD selecionada.</li>" +
                        "<li><b>Excluir FATD (Apenas MASTER):</b> Permite a remoção completa de um processo e todos os seus arquivos associados " +
                        "do sistema.</li>" +
                        "</ul>" +
                        "</html>"
        );

        conteudoAjuda.put("Controle de Prazos",
                "<html>" +
                        "<h3>Entendendo o Controle de Prazos</h3>" +
                        "<p>Esta tela é o painel de controle para todos os processos em andamento, garantindo que os prazos legais sejam cumpridos.</p>" +
                        "<ul>" +
                        "<li><b>Data de Início:</b> Quando a FATD foi gerada.</li>" +
                        "<li><b>Prazo Defesa:</b> 3 dias úteis após a data de início. Prazo para o militar apresentar sua defesa por escrito.</li>" +
                        "<li><b>Prazo Decisão:</b> 8 dias úteis após o fim do prazo de defesa. Prazo para a autoridade competente emitir sua " +
                        "decisão final.</li>" +
                        "</ul>" +
                        "<h4>Edição de Data</h4>" +
                        "<p>Para usuários <b>MASTER</b> ou <b>PARTICIPANTE</b>, é possível corrigir a data de início de um processo. Para isso, " +
                        "selecione a linha desejada na tabela e clique no botão 'Editar Data de Início'. O sistema recalculará todos os prazos " +
                        "automaticamente.</p>" +
                        "</html>"
        );

        conteudoAjuda.put("Configurações",
                "<html>" +
                        "<h3>Ajustando as Configurações</h3>" +
                        "<p>Esta tela permite personalizar o comportamento e os dados base do sistema.</p>" +
                        "<ul>" +
                        "<li><b>Definir próximo Nº de Processo:</b> Permite ajustar o contador sequencial de FATDs. Útil para corrigir a numeração" +
                        " caso algum processo seja cancelado ou para alinhar com uma numeração já existente.</li>" +
                        "<li><b>Gerir Feriados:</b> Cadastre feriados nacionais, estaduais ou municipais. Estes dias serão ignorados no cálculo " +
                        "de dias úteis dos prazos.</li>" +
                        "<li><b>Gerenciar Militar Participante:</b> Cadastre os militares (geralmente Oficiais ou Sargentos) que podem figurar " +
                        "como 'participantes' do fato ao gerar uma FATD.</li>" +
                        "<li><b>Gerenciar Autoridade Competente:</b> Cadastre as autoridades (geralmente Comandantes) que podem assinar e dar a" +
                        " solução em um processo de punição.</li>" +
                        "<li><b>Zoom:</b> Aumente ou diminua o tamanho da fonte de todo o aplicativo. É necessário reiniciar o programa para que" +
                        " a alteração tenha efeito em todas as telas.</li>" +
                        "</ul>" +
                        "</html>"
        );

        conteudoAjuda.put("Gerenciar Usuários",
                "<html>" +
                        "<h3>Como Gerenciar Usuários (Apenas MASTER)</h3>" +
                        "<p>Esta tela permite criar, editar e excluir usuários do sistema.</p>" +
                        "<b>Para criar um novo usuário:</b>" +
                        "<ol>" +
                        "<li>Clique em 'Novo'.</li>" +
                        "<li>Preencha o nome de usuário e a senha.</li>" +
                        "<li>Selecione o Nível de Acesso (Role) apropriado.</li>" +
                        "<li>Clique em 'Salvar'.</li>" +
                        "</ol>" +
                        "<b>Para editar um usuário existente:</b>" +
                        "<ol>" +
                        "<li>Selecione o usuário na lista da esquerda.</li>" +
                        "<li>Altere os dados no formulário à direita.</li>" +
                        "<li>Se desejar manter a senha atual, simplesmente deixe o campo de senha em branco.</li>" +
                        "<li>Clique em 'Salvar'.</li>" +
                        "</ol>" +
                        "</html>"
        );
    }
}