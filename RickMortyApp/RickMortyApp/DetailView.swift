//
//  DetailView.swift
//  RickMortyApp
//
//  Created by Fernando Sales on 13/12/25.
//

import SwiftUI
import RickMortyKMP


struct DetailView: View {
  let character: RickMortyKMP.Character
  
  var body: some View {
    ScrollView {
      VStack(spacing: 20) {
        // 1. Imagem Grande com Hero Effect
        AsyncImage(url: URL(string: character.image)) { phase in
          if let image = phase.image {
            image
              .resizable()
              .aspectRatio(contentMode: .fill)
              .frame(maxWidth: .infinity)
              .frame(height: 300)
              .clipped()
          } else if phase.error != nil {
            Rectangle()
              .fill(Color.gray.opacity(0.3))
              .frame(height: 300)
              .overlay(Image(systemName: "exclamationmark.triangle"))
          } else {
            Rectangle()
              .fill(Color.gray.opacity(0.1))
              .frame(height: 300)
              .overlay(ProgressView())
          }
        }
        
        // 2. Conteúdo de Texto
        VStack(alignment: .leading, spacing: 16) {
          
          // Cabeçalho
          Text(character.name)
            .font(.largeTitle)
            .fontWeight(.bold)
          
          Divider()
          
          // Grid de Informações
          // Usando um layout simples de Key-Value
          InfoRow(icon: "heart.fill", label: "Status", value: character.status, color: statusColor)
          InfoRow(icon: "pawprint.fill", label: "Espécie", value: character.species)
          
          // No futuro, se mapearmos mais campos no KMP (como Origin, Gender), adicionamos aqui.
        }
        .padding()
      }
    }
    .navigationBarTitleDisplayMode(.inline)
  }
  
  private var statusColor: Color {
    switch character.status {
      case "Alive": return .green
      case "Dead": return .red
      default: return .orange
    }
  }
}

// Componente auxiliar para as linhas de informação
struct InfoRow: View {
  let icon: String
  let label: String
  let value: String
  var color: Color = .primary
  
  var body: some View {
    HStack {
      Image(systemName: icon)
        .foregroundColor(color)
        .frame(width: 30)
      
      Text(label)
        .foregroundColor(.secondary)
      
      Spacer()
      
      Text(value)
        .fontWeight(.semibold)
    }
    .font(.body)
  }
}

#Preview {
  let mock = RickMortyKMP.Character(
    id: 1,
    name: "Rick Sanchez",
    status: "Alive",
    species: "Human",
    image: "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
  )
  DetailView(character: mock)
}
