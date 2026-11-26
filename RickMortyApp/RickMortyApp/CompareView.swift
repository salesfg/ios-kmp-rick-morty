//
//  CompareView.swift
//  RickMortyApp
//

import SwiftUI
import RickMortyKMP

struct CompareView: View {
  let comparison: CharacterComparison
  
  init(first: RickMortyKMP.Character, second: RickMortyKMP.Character) {
    self.comparison = CharacterComparator.shared.compare(first: first, second: second)
  }
  
  var body: some View {
    ScrollView {
      VStack(spacing: 24) {
        HStack(alignment: .top, spacing: 16) {
          CompareHeader(character: comparison.first)
          Text("VS")
            .font(.title2)
            .fontWeight(.heavy)
            .foregroundStyle(.secondary)
            .padding(.top, 50)
          CompareHeader(character: comparison.second)
        }
        
        similarityBadge
        
        VStack(spacing: 0) {
          ForEach(comparison.attributes, id: \.label) { attribute in
            CompareAttributeRow(attribute: attribute)
            if attribute.label != comparison.attributes.last?.label {
              Divider()
            }
          }
        }
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
      }
      .padding()
    }
    .navigationTitle("Comparativo")
    .navigationBarTitleDisplayMode(.inline)
  }
  
  private var similarityBadge: some View {
    VStack(spacing: 4) {
      Text("\(comparison.similarityPercent)%")
        .font(.system(size: 40, weight: .bold, design: .rounded))
      Text("\(comparison.matchCount) de \(comparison.totalCount) atributos em comum")
        .font(.subheadline)
        .foregroundStyle(.secondary)
    }
  }
}

private struct CompareHeader: View {
  let character: RickMortyKMP.Character
  
  var body: some View {
    VStack(spacing: 8) {
      AsyncImage(url: URL(string: character.image)) { phase in
        if let image = phase.image {
          image.resizable().aspectRatio(contentMode: .fill)
        } else if phase.error != nil {
          Color.gray.opacity(0.3).overlay(Image(systemName: "exclamationmark.triangle"))
        } else {
          Color.gray.opacity(0.1).overlay(ProgressView())
        }
      }
      .frame(width: 120, height: 120)
      .clipShape(RoundedRectangle(cornerRadius: 16))
      
      Text(character.name)
        .font(.headline)
        .multilineTextAlignment(.center)
        .lineLimit(2)
    }
    .frame(maxWidth: .infinity)
  }
}

private struct CompareAttributeRow: View {
  let attribute: ComparisonAttribute
  
  var body: some View {
    VStack(spacing: 6) {
      HStack(spacing: 4) {
        Image(systemName: attribute.isMatch ? "equal.circle.fill" : "notequal.circle.fill")
          .foregroundStyle(attribute.isMatch ? .green : .orange)
        Text(attribute.label)
          .font(.caption)
          .foregroundStyle(.secondary)
      }
      HStack {
        Text(attribute.firstValue)
          .fontWeight(.semibold)
          .frame(maxWidth: .infinity)
        Text(attribute.secondValue)
          .fontWeight(.semibold)
          .frame(maxWidth: .infinity)
      }
    }
    .padding()
  }
}

#Preview {
  NavigationView {
    CompareView(
      first: RickMortyKMP.Character(id: 1, name: "Rick Sanchez", status: "Alive", species: "Human", image: "https://rickandmortyapi.com/api/character/avatar/1.jpeg"),
      second: RickMortyKMP.Character(id: 2, name: "Morty Smith", status: "Alive", species: "Human", image: "https://rickandmortyapi.com/api/character/avatar/2.jpeg")
    )
  }
}
