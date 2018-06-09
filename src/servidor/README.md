#computacao_distribuida_INE5625
#algoritmo de exclusao mutua
#Servidor Central:

. Um processo é eleito coordenador (Líder)

. Para entrar em uma região crítica (RC), um processo envia uma mensagem de requisição para o servidor/líder e espera uma resposta.

. A resposta constitui uma ficha (token) significando permissão para entrar na RC.

. Na saída da RC, o processo devolve a ficha para o servidor.

. Quando a ficha já está com algum processo, os demais são enfileirados, aguardando a liberação da RC.

. Quando a RC é liberada, a ficha é devolvida ao servidor. Este, então, escolhe o processo mais antigo da fila e entrega a ficha para este processo.

. A entrada na RC exige duas mensagens:
requisição e concessão.

OBS
. Servidor pode se tornar um gargalo de desempenho.
