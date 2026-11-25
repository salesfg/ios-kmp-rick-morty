//
//  ContentView.swift
//  RickMortyApp
//
//  Created by Fernando Sales on 04/12/25.
//

import SwiftUI
import RickMortyKMP

struct ContentView: View {
  @StateObject var viewModel = CharacterListObservable()
  @State private var query = ""
  
  var body: some View {
    NavigationView {
      Group {
        if viewModel.isLoading {
          ProgressView("Loading...")
        } else if viewModel.characters.isEmpty {
          Text("Nothing found.")
        } else {
          List {
            ForEach(viewModel.characters, id: \.id) { character in
              NavigationLink(destination: DetailView(character: character, viewModel: viewModel)) {
                CharacterRow(character: character, isFavorite: viewModel.isFavorite(id: character.id))
              }
            }
          }
        }
      }
      .navigationTitle("Rick and Morty Characters")
      .searchable(text: $query, prompt: "Buscar personagem")
      .onChange(of: query) { _, newValue in
        viewModel.search(newValue)
      }
      .toolbar {
        NavigationLink(destination: FavoritesView()) {
          Image(systemName: "heart.fill")
        }
      }
    }
  }
}

#Preview {
    ContentView()
}
