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
        if viewModel.characters.isEmpty {
          if viewModel.isLoading {
            ProgressView("Loading...")
          } else if let error = viewModel.errorMessage {
            Text(error)
              .foregroundStyle(.secondary)
          } else {
            Text("Nothing found.")
          }
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
      .safeAreaInset(edge: .bottom) {
        if let error = viewModel.errorMessage, !viewModel.characters.isEmpty {
          Text(error)
            .font(.footnote)
            .padding(8)
            .frame(maxWidth: .infinity)
            .background(.red.opacity(0.15))
        }
      }
      .navigationTitle("Rick and Morty Characters")
      .searchable(text: $query, prompt: "Buscar personagem")
      .onChange(of: query) { _, newValue in
        viewModel.search(newValue)
      }
      .toolbar {
        if viewModel.isLoading && !viewModel.characters.isEmpty {
          ToolbarItem(placement: .topBarLeading) {
            ProgressView()
          }
        }
        ToolbarItem(placement: .topBarTrailing) {
          NavigationLink(destination: FavoritesView(viewModel: viewModel)) {
            Image(systemName: "heart.fill")
          }
        }
      }
    }
  }
}

#Preview {
    ContentView(viewModel: .preview())
}
