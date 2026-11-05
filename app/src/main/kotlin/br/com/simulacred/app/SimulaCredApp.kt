package br.com.simulacred.app

import android.app.Application
import br.com.simulacred.app.data.SimulacaoRepository
import br.com.simulacred.app.data.SimulacaoRepositoryLocal

/**
 * Application do app. Por enquanto guarda as dependências à mão; no bloco do
 * Dagger 2 isto vira o `AppComponent` e os ViewModels passam a vir da
 * `ViewModelFactory` injetada.
 */
class SimulaCredApp : Application() {
    val simulacaoRepository: SimulacaoRepository by lazy { SimulacaoRepositoryLocal() }
}
